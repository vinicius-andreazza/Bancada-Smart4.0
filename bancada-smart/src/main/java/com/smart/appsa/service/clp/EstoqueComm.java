package com.smart.appsa.service.clp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.mapper.clp.EstoquePlcMapper;
import com.smart.appsa.model.plc.EstoquePlc;
import com.smart.appsa.service.EstoqueService;
import com.smart.appsa.service.clp.reader.PlcReader;

@Service
public class EstoqueComm {

    private PlcReader plcReader;

    private final PlcConnectionService plcConnectionService;

    private final EstoqueService estoqueService;

    private final EstoquePlc estoquePlc;

    private String ip = "10.74.241.10";
    
    private final ExecutorService estoquePool = Executors.newSingleThreadExecutor();

    private static final int DELAY = 600;
    private static final int DB_ESTOQUE = 9;
    private static final int OFFSET_INICIAR_PEDIDO = 62;
    private static final int OFFSET_GERENCIAMENTO_ESTOQUE = 64;
    private static final int OFFSET_POSICAO_GUARDAR = 66;

    public EstoqueComm(PlcConnectionService plcConnectionService, EstoqueService estoqueService, EstoquePlc estoquePlc) {
        this.plcConnectionService = plcConnectionService;
        this.estoqueService = estoqueService;
        this.estoquePlc = estoquePlc;
    }

    public void startComm(String ip) {
        this.plcReader = new PlcReader(plcConnectionService.getConnection(ip), "Estoque", DB_ESTOQUE, 0, 108,
                data -> handleData(data, ip));
        estoquePool.execute(plcReader);
    }

    private void handleData(byte[] data, String ip) {
        this.ip = ip;

        EstoquePlcMapper.updateData(data, estoquePlc);

        validarPedido();
        validarOperacao();
        validarRetirada();
        validarAdicao();
        validarIniciarGuardar();
        retornarPosicao();

        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void validarIniciarGuardar() {
        if ((estoquePlc.isOcupado() || estoquePlc.isRetornoEstoqueCheio())
                & estoquePlc.isIniciarGuardar() == true) {
            try {
                getConnector().writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, 1, false); // Coloca iniciarGuardarEst em FALSE

            } catch (Exception e) {
                System.out.println(
                        "ERRO: Atualização da Flag IniciarGuardarEstoque [DBDB_ESTOQUE:OFFSET_GERENCIAMENTO_ESTOQUE.1] para FALSE");
            }

        }
    }

    private void retornarPosicao() {
        if (estoquePlc.isPedirPosicao() && !estoquePlc.isOcupado()) {


            int posEstoqueLivre = estoqueService.findFirstByCor(0).getCor();

            if (posEstoqueLivre > 0) {

                try {
                    getConnector().writeInt(DB_ESTOQUE, OFFSET_POSICAO_GUARDAR, posEstoqueLivre);
                } catch (Exception e) {
                    System.out.println("ERRO: Atualização da PosicaoGuardarEstoque [DBDB_ESTOQUE:OFFSET_POSICAO_GUARDAR]");
                }

                try {
                    getConnector().writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, 1, true); // coloca IniciarGuardar em TRUE
                } catch (Exception e) {
                    System.out.println("ERRO: Atualização da Flag IniciarGuardarEstoque [DBDB_ESTOQUE:OFFSET_GERENCIAMENTO_ESTOQUE.1]");
                }
            } else {
                System.out.println("ERRO: Nao existe posição livre.");
            }
        }
    }

    private void validarRetirada() {
        if ((estoquePlc.getPosicaoEstoque() > 0) && estoquePlc.isRemoverEstoque() ) {
            try {
                getConnector().writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, 0, true);
            } catch (Exception e) {
                System.out.println("ERRO: Atualização da Flag RecebidoEstoque [DBDB_ESTOQUE:OFFSET_GERENCIAMENTO_ESTOQUE.0] para TRUE");
            }

            byte offsetPosicao = (byte) (68 + (estoquePlc.getPosicaoEstoque() - 1));

            try {

                getConnector().writeByte(DB_ESTOQUE, offsetPosicao, (byte) 0);
                estoqueService.put(estoquePlc.getPosicaoEstoque(), EstoqueDTO.builder().cor(0).build());

            } catch (Exception e) {
                System.out.println("ERRO: Na tentativa de remover do Estoque");
                e.printStackTrace();
            }
        }
    }

    private void validarAdicao() {
        if ((estoquePlc.getPosicaoEstoque() > 0) && estoquePlc.isAdicionarEstoque()) {
            try {
                getConnector().writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, 0, true); // coloca RecebidoEstoque em TRUE
            } catch (Exception e) {
                System.out.println("ERRO: Atualização da Flag RecebidoEstoque [DBDB_ESTOQUE:OFFSET_GERENCIAMENTO_ESTOQUE.0] para TRUE");
            }

            byte offset = (byte) (68 + (estoquePlc.getPosicaoEstoque() - 1));

            try {
                getConnector().writeByte(DB_ESTOQUE, offset, (byte) estoquePlc.getCorGuardarEstoque());
                estoqueService.put(estoquePlc.getPosicaoEstoque(), EstoqueDTO.builder().cor(estoquePlc.getCorGuardarEstoque()).build());

            } catch (Exception e) {
                System.out.println("ERRO: Na tentativa de adicionar no Estoque");
                e.printStackTrace();
            }
        }
    }

    private void validarPedido() {
        
        if (estoquePlc.isIniciarPedido() && estoquePlc.isOcupado()) {
            try {
                getConnector().writeBit(DB_ESTOQUE, OFFSET_INICIAR_PEDIDO, 0, false); // coloca IniciarPedido em FALSE
            } catch (Exception e) {
                System.out.println(
                        "ERRO [iniciarPedido == true & ocupadoEst == true]: Atualização da Flag IniciarPedido [DBDB_ESTOQUE:OFFSET_INICIAR_PEDIDO.0] para FALSE");
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
        if (!estoquePlc.isStartOp() && !estoquePlc.isCancelOp() && !estoquePlc.isFinishOp()) {
            try {
                getConnector().writeBit(DB_ESTOQUE, 0, 0, false);
            } catch (Exception e) {
                System.out.println("ERRO: Atualização da Flag RecebidoOPEstoque [DBDB_ESTOQUE:0.0] para FALSE");
            }
        }
    }

    private void validarInicioDaOperacao() {
        
        if (estoquePlc.isStartOp() && !estoquePlc.isRecebidoOP()) {
            try {
                getConnector().writeBit(DB_ESTOQUE, 0, 0, true); // coloca RecebidoOPEst em TRUE
            } catch (Exception e) {
                System.out.println(
                        "ERRO [startOp]: Atualização da Flag RecebidoOPEstoque [DBDB_ESTOQUE:0.0] para TRUE");
            }
        }
    }

    private void validarFinalizacaoDaOperacao() {
        
        if (estoquePlc.isFinishOp() && !estoquePlc.isRecebidoOP()) {
            try {
                getConnector().writeBit(DB_ESTOQUE, 0, 0, true); // coloca RecebidoOPEst em TRUE
            } catch (Exception e) {
                System.out.println(
                        "ERRO [finishOp]: Atualização da Flag RecebidoOPEstoque [DBDB_ESTOQUE:0.0] para TRUE");
            }
        }
    }

    private void validarOperacaoDeRecepcao() {
        
        if (!estoquePlc.isRemoverEstoque() && !estoquePlc.isAdicionarEstoque()) {
            try {
                getConnector().writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, 0, false); // coloca RecebidoEstoque em FALSE
            } catch (Exception e) {
                System.out.println("ERRO: Atualização da Flag RecebidoEstoque [DBDB_ESTOQUE:OFFSET_GERENCIAMENTO_ESTOQUE.0] para FALSE");
            }
        }
    }

    private PlcConnector getConnector() {
        return plcConnectionService.getConnection(ip);
    }

}