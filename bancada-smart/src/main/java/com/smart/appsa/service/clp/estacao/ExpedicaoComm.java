package com.smart.appsa.service.clp.estacao;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.ipconfig.ExpedicaoIp;
import com.smart.appsa.dto.ExpedicaoDTO;
import com.smart.appsa.event.ExpedicaoLiberadaEvent;
import com.smart.appsa.event.ExpedicaoReservadaEvent;
import com.smart.appsa.mapper.clp.ExpedicaoPlcMapper;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.clp.ExpedicaoPlc;
import com.smart.appsa.service.ExpedicaoService;
import com.smart.appsa.service.PedidoService;
import com.smart.appsa.service.ProducaoService;
import com.smart.appsa.service.clp.poller.PlcPoller;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExpedicaoComm {
    private final ExpedicaoIp expedicaoIp;
    private final ExpedicaoService expedicaoService;
    private final PedidoService pedidoService;
    private final ProducaoService producaoService;

    private final PlcPoller poller;

    private int opAntiga = 0;
    private int opCancelada = 0;

    private final PlcConnectionService plcConnectionService;

    private final ExpedicaoPlc expedicaoPlc;

    private static final int DELAY = 400;
    private static final int DB_EXPEDICAO = 9;
    private static final int OFFSET_STATUS_OP = 0;
    private static final int OFFSET_GERENCIAMENTO_EXPEDICAO = 2;
    private static final int OFFSET_POSICAO_GUARDAR = 4;

    public ExpedicaoComm(PlcConnectionService plcConnectionService,
            ExpedicaoPlc expedicaoPlc, ExpedicaoService expedicaoService, PedidoService pedidoService,
            ProducaoService producaoService, ExpedicaoIp expedicaoIp) {
        this.poller = new PlcPoller(plcConnectionService);
        this.plcConnectionService = plcConnectionService;
        this.expedicaoPlc = expedicaoPlc;
        this.expedicaoService = expedicaoService;
        this.pedidoService = pedidoService;
        this.producaoService = producaoService;
        this.expedicaoIp = expedicaoIp;
    }

    public void startComm() {
        poller.start(expedicaoIp.getIp(), DELAY, () -> {
            try {
                handleData(getConnector().readBlock(DB_EXPEDICAO, 0, 46));
            } catch (Exception e) {
                log.error("Erro na leitura da expedição: {}", expedicaoIp.getIp(), e);
            }
        });
        atualizarExpedicao();
    }

    public void disconnect() {
        poller.stop();
    }

    public boolean isConnected() {
        return poller.isConnected();
    }

    private void handleData(byte[] data) {

        ExpedicaoPlcMapper.updateData(data, expedicaoPlc);

        validarOperacao();
        validarPosicaoGuardar();
        validarRecepcao();
        validarAdicao();
        validarRetirada();
        validarFinalizacaoOP();
        validarCancelamento();
        concluirPedido();

    }

    private void validarOperacao() {
        if (!expedicaoPlc.isStartOP() && !expedicaoPlc.isFinishOP() && !expedicaoPlc.isCancelOP()) {
            try {
                getConnector().writeBit(DB_EXPEDICAO, OFFSET_STATUS_OP, 0, false);
            } catch (Exception e) {
                log.error(
                        "Atualização da Flag RecebidoOPExpedicao [{}:{}.0] para FALSE",
                        DB_EXPEDICAO,
                        OFFSET_STATUS_OP,
                        e);
            }
        }

        validarRecebido();
    }

    private void validarRecebido() {
        if (expedicaoPlc.isStartOP() && !expedicaoPlc.isRecebidoOP()) {
            try {
                getConnector().writeBit(DB_EXPEDICAO, OFFSET_STATUS_OP, 0, true);
            } catch (Exception e) {
                log.error(
                        "[startOp]: Atualização da Flag RecebidoOPExpedicao [{}:{}.0] para TRUE",
                        DB_EXPEDICAO,
                        OFFSET_STATUS_OP,
                        e);
            }
        }

        if (expedicaoPlc.isFinishOP() && !expedicaoPlc.isRecebidoOP()) {
            try {
                getConnector().writeBit(DB_EXPEDICAO, OFFSET_STATUS_OP, 0, true);
            } catch (Exception e) {
                log.error(
                        "[finishOp]: Atualização da Flag RecebidoOPExpedicao [{}:{}.0] para TRUE",
                        DB_EXPEDICAO,
                        OFFSET_STATUS_OP,
                        e);
            }
        }
    }

    private void validarPosicaoGuardar() {
        if (!expedicaoPlc.isPedirPosicao()) {
            try {
                getConnector().writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, 1, false);

            } catch (Exception e) {
                log.error(
                        "Atualização da Flag IniciarGuardarExpedicao [{}:{}.1] para FALSE",
                        DB_EXPEDICAO,
                        OFFSET_GERENCIAMENTO_EXPEDICAO,
                        e);
            }
            return;
        }

        int posicaoExpedicaoSolicitada = buscarPrimeiraPosicaoLivre();

        try {
            getConnector().writeInt(DB_EXPEDICAO, OFFSET_POSICAO_GUARDAR, posicaoExpedicaoSolicitada);
        } catch (Exception e) {
            log.error(
                    "Atualização da PosicaoGuardarExpedicao [{}:{}]",
                    DB_EXPEDICAO,
                    OFFSET_POSICAO_GUARDAR,
                    e);
        }

        try {
            getConnector().writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, 1, true);
        } catch (Exception e) {
            log.error(
                    "Atualização da Flag IniciarGuardarExpedicao [{}:{}.1] para TRUE",
                    DB_EXPEDICAO,
                    OFFSET_GERENCIAMENTO_EXPEDICAO,
                    e);
        }
    }

    private void validarRecepcao() {
        if (!expedicaoPlc.isAdicionar() && !expedicaoPlc.isRemover()) {
            try {
                getConnector().writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, 0, false);
            } catch (Exception e) {
                log.error(
                        "Atualização da Flag RecebidoExpedicao [{}:{}.0] para FALSE",
                        DB_EXPEDICAO,
                        OFFSET_GERENCIAMENTO_EXPEDICAO,
                        e);
            }
        }
    }

    private void validarAdicao() {
        if (!expedicaoPlc.isAdicionar()) {
            return;
        }

        try {
            getConnector().writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, 0, true);
        } catch (Exception e) {
            log.error(
                    "Atualização da Flag RecebidoExpedicao [{}:{}.0] para TRUE",
                    DB_EXPEDICAO,
                    OFFSET_GERENCIAMENTO_EXPEDICAO,
                    e);
        }

        int posicaoGuardar = expedicaoPlc.getPosicaoGuardar();
        if (posicaoGuardar <= 0) {
            return;
        }

        int offset = 6 + (posicaoGuardar - 1) * 2;
        log.info("Guardando operação na posição {}", posicaoGuardar);

        try {
            getConnector().writeInt(DB_EXPEDICAO, offset, expedicaoPlc.getOpGuardado());

            expedicaoService.assignPedidoByPosition(getPedidoByCod(expedicaoPlc.getOpGuardado()).getId(),
                    posicaoGuardar);

        } catch (Exception e) {
            log.error(
                    "Tentativa de adicionar pedido na posição {}",
                    posicaoGuardar,
                    e);
        }
    }

    private void validarRetirada() {
        if (!expedicaoPlc.isRemover()) {
            return;
        }

        try {
            getConnector().writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, 0, true); // coloca recebidoExpedicao
                                                                                            // em TRUE
        } catch (Exception e) {
            log.error(
                    "Atualização da Flag RecebidoExpedicao [{}:{}.0] para TRUE",
                    DB_EXPEDICAO,
                    OFFSET_GERENCIAMENTO_EXPEDICAO,
                    e);
        }

        int posicaoRemovida = expedicaoPlc.getPosicaoRemovido();
        if (posicaoRemovida <= 0) {
            return;
        }

        int offset = 6 + (posicaoRemovida - 1) * 2;
        log.info("Removendo operação da posição {}", posicaoRemovida);

        try {
            getConnector().writeInt(DB_EXPEDICAO, offset, 0);
            expedicaoService.releasePosicao(Long.parseLong(posicaoRemovida + ""));
        } catch (Exception e) {
            log.error(
                    "Tentativa de remover pedido da posição {}",
                    posicaoRemovida,
                    e);
        }
    }

    private void validarFinalizacaoOP() {
        if (expedicaoPlc.getPosicaoGuardado() == expedicaoPlc.getPosicaoGuardar()
                && !expedicaoPlc.isOcupado()
                && expedicaoPlc.isFinishOP()) {
            updateStatusConcluido();
            log.info("Operação {} finalizada.", expedicaoPlc.getOpGuardado());
        }
    }

    private void concluirPedido() {
        if (((expedicaoPlc.isFinishOP() || !expedicaoPlc.isRecebidoOP())
                && (expedicaoPlc.getOpGuardado() > 0 && expedicaoPlc.getOpGuardado() != opAntiga))) {
            opAntiga = expedicaoPlc.getOpGuardado();
            producaoService.concluirProducao(expedicaoPlc.getOpGuardado());
        }
    }

    private void validarCancelamento() {
        if (expedicaoPlc.isCancelOP() && expedicaoPlc.getOpGuardado() > 0
                && expedicaoPlc.getOpGuardado() != opCancelada) {
            opCancelada = expedicaoPlc.getOpGuardado();
            producaoService.cancelarOuFalharProducao(expedicaoPlc.getOpGuardado(),
                    "cancelamento sinalizado pelo CLP de expedição (CancelOP)");
        }
    }

    @Async("plcExpedicaoWriteExecutor")
    @EventListener
    public void onExpedicaoReservada(ExpedicaoReservadaEvent event) {
        PlcConnector connector = getConnector();
        if (connector == null || !connector.isConnected()) {
            log.warn(
                    "CLP de expedição desconectado. Reserva da posição {} para o pedido {} descartada.",
                    event.getPosicao(),
                    event.getCodPedido());
            return;
        }
        synchronized (connector) {
            try {
                connector.writeInt(DB_EXPEDICAO, 6 + (event.getPosicao() - 1) * 2, event.getCodPedido());
            } catch (Exception e) {
                log.error(
                        "Reserva da expedição na posição {}",
                        event.getPosicao(),
                        e);
            }
        }
    }

    @Async("plcExpedicaoWriteExecutor")
    @EventListener
    public void onExpedicaoLiberada(ExpedicaoLiberadaEvent event) {
        PlcConnector connector = getConnector();
        if (connector == null || !connector.isConnected())
            return;
        synchronized (connector) {
            try {
                connector.writeInt(DB_EXPEDICAO, 6 + (event.getPosicao() - 1) * 2, 0);
            } catch (Exception e) {
                log.error(
                        "Liberação da expedição na posição {}",
                        event.getPosicao(),
                        e);
            }
        }
    }

    private void atualizarExpedicao() {
        PlcConnector connector = getConnector();
        List<ExpedicaoDTO> listaExpedicao = expedicaoService.findAll();
        listaExpedicao.forEach(e -> {
            try {
                if(e.pedido()!=null){
                    connector.writeInt(DB_EXPEDICAO, 6 + (e.posicao() - 1) * 2, e.pedido().codPedido().intValue());
                }
            } catch (Exception e1) {
                log.error(
                        "Atualizar expedição na posição {}",
                        e.posicao(),
                        e1);
            }
        });
    }

    private void updateStatusConcluido() {
        expedicaoPlc.setConcluidoOP(true);
    }

    private int buscarPrimeiraPosicaoLivre() {
        return expedicaoService.findFirstAvailable().getPosicao();
    }

    private Pedido getPedidoByCod(int codigo) {
        return pedidoService.findPedidoByCodigo(codigo);
    }

    private PlcConnector getConnector() {
        return plcConnectionService.getConnection(expedicaoIp.getIp());
    }
}
