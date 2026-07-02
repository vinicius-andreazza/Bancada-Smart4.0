package com.smart.appsa.service.clp.estacao;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.ipconfig.EstoqueIp;
import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.dto.ExpedicaoDTO;
import com.smart.appsa.event.EstoqueAtualizadoEvent;
import com.smart.appsa.mapper.clp.EstoquePlcMapper;
import com.smart.appsa.model.clp.EstoquePlc;
import com.smart.appsa.service.EstoqueService;
import com.smart.appsa.service.clp.poller.PlcPoller;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EstoqueComm {

    private final EstoqueIp estoqueIp;

    private final PlcPoller poller;

    private final PlcConnectionService plcConnectionService;

    private final EstoqueService estoqueService;

    private final EstoquePlc estoquePlc;

    private static final int DELAY = 600;
    private static final int DB_ESTOQUE = 9;
    private static final int OFFSET_INICIAR_PEDIDO = 62;
    private static final int OFFSET_GERENCIAMENTO_ESTOQUE = 64;
    private static final int OFFSET_POSICAO_GUARDAR = 66;

    public EstoqueComm(PlcConnectionService plcConnectionService, EstoqueService estoqueService, EstoquePlc estoquePlc,
            EstoqueIp estoqueIp) {
        this.poller = new PlcPoller(plcConnectionService);
        this.plcConnectionService = plcConnectionService;
        this.estoqueService = estoqueService;
        this.estoquePlc = estoquePlc;
        this.estoqueIp = estoqueIp;
    }

    public void startComm() {
        poller.start(estoqueIp.getIp(), DELAY, () -> {
            try {
                handleData(getConnector().readBlock(DB_ESTOQUE, 0, 110));
            } catch (Exception e) {
                log.error("Erro na leitura do estoque: {}", estoqueIp.getIp(), e);
            }
        });
        atualizarEstoque();
    }

    public void disconnect() {
        poller.stop();
    }

    public boolean isConnected() {
        return poller.isConnected();
    }

    private void handleData(byte[] data) {
        EstoquePlcMapper.updateData(data, estoquePlc);

        validarPedido();
        validarOperacao();
        validarRetirada();
        validarAdicao();
        validarIniciarGuardar();
        retornarPosicao();
    }

    private void validarIniciarGuardar() {
        if ((estoquePlc.isOcupado() || estoquePlc.isRetornoEstoqueCheio())
                & estoquePlc.isIniciarGuardar() == true) {
            try {
                getConnector().writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, 1, false); // Coloca iniciarGuardarEst
                                                                                             // em FALSE

            } catch (Exception e) {
                log.error(
                        "Atualização da Flag IniciarGuardarEstoque [{}:{}.1] para FALSE", DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, e);
            }

        }
    }

    private void retornarPosicao() {
        if (estoquePlc.isPedirPosicao() && !estoquePlc.isOcupado()) {

            int posEstoqueLivre = estoqueService.findFirstByCor(0).getPosicao();
            log.info("Estoque livre: "+posEstoqueLivre);
            if (posEstoqueLivre > 0) {

                try {
                    getConnector().writeInt(DB_ESTOQUE, OFFSET_POSICAO_GUARDAR, posEstoqueLivre);
                } catch (Exception e) {
                    log.error(
                            "Atualização da PosicaoGuardarEstoque [{}:{}]", DB_ESTOQUE, OFFSET_POSICAO_GUARDAR, e);
                }

                try {
                    getConnector().writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, 1, true); // coloca IniciarGuardar
                                                                                                // em TRUE
                } catch (Exception e) {
                    log.error(
                            "Atualização da Flag IniciarGuardarEstoque [{}:{}.1]", DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, e);
                }
            } else {
                log.error("Nao existe posição livre.");
            }
        }
    }

    private void validarRetirada() {
        if ((estoquePlc.getPosicaoEstoque() > 0) && estoquePlc.isRemoverEstoque()) {
            try {
                getConnector().writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, 0, true);
            } catch (Exception e) {
                log.error(
                        "Atualização da Flag RecebidoEstoque [{}:{}.0] para TRUE", DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, e);
            }

            byte offsetPosicao = (byte) (68 + (estoquePlc.getPosicaoEstoque() - 1));

            try {

                getConnector().writeByte(DB_ESTOQUE, offsetPosicao, (byte) 0);
                estoqueService.releasePosition(estoquePlc.getPosicaoEstoque());
                

            } catch (Exception e) {
                log.error("Tentativa de remoção da posição {}",offsetPosicao,e);
            }
        }
    }

    private void validarAdicao() {
        if ((estoquePlc.getPosicaoEstoque() > 0) && estoquePlc.isAdicionarEstoque()) {
            try {
                getConnector().writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, 0, true); // coloca RecebidoEstoque em
                                                                                            // TRUE
            } catch (Exception e) {
                log.error(
                        "Atualização da Flag RecebidoEstoque [{}_{}.0] para TRUE", DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, e);
            }

            byte offset = (byte) (68 + (estoquePlc.getPosicaoEstoque() - 1));

            try {
                getConnector().writeByte(DB_ESTOQUE, offset, (byte) estoquePlc.getCorGuardarEstoque());
                estoqueService.addPosition(estoquePlc.getPosicaoEstoque(), estoquePlc.getCorGuardarEstoque());
            } catch (Exception e) {
                log.error("Tentativa de adição na posição {}", estoquePlc.getPosicaoEstoque(), e);
            }
        }
    }

    private void validarPedido() {

        if (estoquePlc.isIniciarPedido() && estoquePlc.isOcupado()) {
            try {
                getConnector().writeBit(DB_ESTOQUE, OFFSET_INICIAR_PEDIDO, 0, false); // coloca IniciarPedido em FALSE
            } catch (Exception e) {
                log.error(
                        "[iniciarPedido == true & ocupadoEst == true]: Atualização da Flag IniciarPedido [{}:{}.0] para FALSE", DB_ESTOQUE, OFFSET_INICIAR_PEDIDO, e);
            }
        }

    }

    private void validarOperacao() {
        validarOperacaoPelasFlags();
        validarInicioDaOperacao();
        validarFinalizacaoDaOperacao();
        validarOperacaoDeRecepcao();
    }

    private void validarOperacaoPelasFlags() {
        if (!estoquePlc.isStartOP() && !estoquePlc.isCancelOP() && !estoquePlc.isFinishOP()) {
            try {
                getConnector().writeBit(DB_ESTOQUE, 0, 0, false);
            } catch (Exception e) {
                log.error("Atualização da Flag RecebidoOPEstoque [{}:0.0] para FALSE", DB_ESTOQUE, e);
            }
        }
    }

    private void validarInicioDaOperacao() {

        if (estoquePlc.isStartOP() && !estoquePlc.isRecebidoOP()) {
            try {
                getConnector().writeBit(DB_ESTOQUE, 0, 0, true); // coloca RecebidoOPEst em TRUE
            } catch (Exception e) {
                log.error(
                        "[startOp]: Atualização da Flag RecebidoOPEstoque [{}:0.0] para TRUE", DB_ESTOQUE, e);
            }
        }
    }

    private void validarFinalizacaoDaOperacao() {
        if(estoquePlc.isFinishOP()){
            updateStatusConcluido();
        }

        if (estoquePlc.isFinishOP() && !estoquePlc.isRecebidoOP()) {
            try {
                getConnector().writeBit(DB_ESTOQUE, 0, 0, true); // coloca RecebidoOPEst em TRUE
            } catch (Exception e) {
                log.error(
                        "[finishOp]: Atualização da Flag RecebidoOPEstoque [{}:0.0] para TRUE", DB_ESTOQUE, e);
            }
        }
    }

    private void validarOperacaoDeRecepcao() {

        if (!estoquePlc.isRemoverEstoque() && !estoquePlc.isAdicionarEstoque()) {
            try {
                getConnector().writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, 0, false); // coloca RecebidoEstoque
                                                                                             // em FALSE
            } catch (Exception e) {
                log.error(
                        "Atualização da Flag RecebidoEstoque [{}:{}.0] para FALSE", DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, e);
            }
        }
    }

    @Async("plcEstoqueWriteExecutor")
    @EventListener
    @Order(1)
    public void onEstoqueAtualizado(EstoqueAtualizadoEvent event) {
        PlcConnector connector = getConnector();
        if (connector == null || !connector.isConnected())
            return;
        synchronized (connector) {
            try {
                connector.writeByte(DB_ESTOQUE, 67 + event.getPosicao(), (byte) event.getCor());
            } catch (Exception e) {
                log.error("write estoque posicao {}", event.getPosicao(), e);
            }
        }
    }

    private void atualizarEstoque() {
        PlcConnector connector = getConnector();
        List<EstoqueDTO> listaEstoque = estoqueService.findAll();
        listaEstoque.forEach(e -> {
            try {
                connector.writeByte(DB_ESTOQUE, 67 + e.posicao(), (byte) e.cor().intValue());
            } catch (Exception ex) {
                log.error("Atualizar estoque na posição {}", e.posicao(), ex);
            }
        });
    }

    private void updateStatusConcluido() {
        estoquePlc.setConcluidoOP(true);
    }

    private PlcConnector getConnector() {
        return plcConnectionService.getConnection(estoqueIp.getIp());
    }

}