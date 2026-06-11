package com.smart.appsa.service.clpcomm;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.model.plc.EstoquePlc;
import com.smart.appsa.service.SmartService;
import com.smart.appsa.service.clpcomm.reader.PlcReader;

@Service
public class EstoqueComm {

    private PlcReader plcReader;

    private final PlcConnectionService plcConnectionService;

    private EstoquePlc estoquePlc;

    public EstoqueComm(PlcConnectionService plcConnectionService) {
        this.plcConnectionService = plcConnectionService;
    }

    public void startComm(String ip) {
        this.plcReader = new PlcReader(plcConnectionService.getConnection(ip), "Estoque", 9, 0, 108,
                data -> handleData(data, ip));
        plcReader.run();
    }

    private void handleData(byte[] data, String ip) {
        PlcConnector plcConnector = plcConnectionService.getConnection(ip);
        estoquePlc.setRecebidoOP((data[0] & 0x01) != 0);

        estoquePlc.setIniciarPedido((data[62] & (byte) 0x01) != 0);
        estoquePlc.setRecebidoEstoque((data[64] & 0x01) != 0);
        estoquePlc.setIniciarGuardar((data[64] & 0x02) != 0);

        estoquePlc.setPosicaoGuardar(((data[66] & 0xFF) << 8) | (data[67] & 0xFF));

        byte[] posicoes = new byte[28];
        for (int c = 0; c < 28; c++) {
            posicoes[c] = data[68 + c];
        }

        estoquePlc.setPosicoes(posicoes);

        estoquePlc.setNumeroPedido(((data[96] & 0xFF) << 8) | (data[97] & 0xFF));
        estoquePlc.setCancelOp((data[98] & 0x01) != 0);
        estoquePlc.setFinishOp((data[98] & 0x02) != 0);
        estoquePlc.setStartOp((data[98] & 0x04) != 0);

        estoquePlc.setOcupado((data[100] & 0x01) != 0);
        estoquePlc.setAguardando((data[100] & 0x02) != 0);
        estoquePlc.setManual((data[100] & 0x04) != 0);
        estoquePlc.setEmergencia((data[100] & 0x08) != 0);

        estoquePlc.setPedirPosicao((data[102] & 0x01) != 0);
        estoquePlc.setPosicaoEstoque(((data[104] & 0xFF) << 8) | (data[105] & 0xFF));
        estoquePlc.setAdicionarEstoque((data[106] & 0x01) != 0);

        estoquePlc.setRemoverEstoque((boolean) ((data[106] & 0x02) != 0));

        estoquePlc.setRetornoEstoqueCheio((data[106] & 0x04) != 0);
        estoquePlc.setCorGuardarEstoque(((data[108] & 0xFF) << 8) | (data[109] & 0xFF));

        estoquePlc.setRemoverEstoque((data[106] & 0x02) != 0);

        validarPedido(plcConnector);
        validarOperacao(plcConnector);
        validarRetirada(plcConnector);
        validarAdicao(plcConnector);
    }

    private void validarRetirada(PlcConnector plcConnector) {
        if ((estoquePlc.getPosicaoEstoque() > 0) && estoquePlc.isRemoverEstoque() == true) {
            try {
                plcConnector.writeBit(9, 64, 0, true);
            } catch (Exception e) {
                System.out.println("ERRO: Atualização da Flag RecebidoEstoque [DB9:64.0] para TRUE");
            }

            byte offset = (byte) (68 + (estoquePlc.getPosicaoEstoque() - 1));

            try {
                // Atualiza cor no CLP
                System.out
                        .println("\n\nREMOVENDO ESTOQUE NA POSIÇÃO: " + offset + " COR: " + estoquePlc.getCorGuardarEstoque() + "\n\n");
                plcConnector.writeByte(9, offset, (byte) 0);

                // Cria mapa de dados com apenas uma posição
                Map<String, Integer> dadosMap = new HashMap<>();
                dadosMap.put("posicao:" + estoquePlc.getPosicaoEstoque(), 0);

                // === Chama serviço de integração ===
                //boolean sucesso = apiIntegrationService.salvarEstoque(dadosMap);
                /*
                if (sucesso) {
                    System.out.println("Estoque salvo com sucesso na API.");
                } else {
                    System.out.println("Falha ao salvar estoque na API.");
                    // aqui você poderia lançar uma exceção ou marcar para tentar novamente
                } */

            } catch (Exception e) {
                System.out.println("ERRO: Na tentativa de remover do Estoque");
                e.printStackTrace();
            }
        }
    }

    private void validarAdicao(PlcConnector plcConnector) {
        if ((estoquePlc.getPosicaoEstoque() > 0) && estoquePlc.isAdicionarEstoque() == true) {
            //eventos.add("Seq " + seq++ + ": (posicaoEstoque > 0) & adicionarEstoque == true");

                //System.out.println("Flag: RecebidoEstoque_TRUE - ADICIONAR ESTOQUE");
                // Coloca a flag RecebidoEstoque em TRUE
                try {
                    //eventos.add("Seq " + seq++ + ": coloca RecebidoEstoque em TRUE");
                    plcConnector.writeBit(9, 64, 0, Boolean.parseBoolean("TRUE")); // coloca RecebidoEstoque em TRUE
                } catch (Exception e) {
                    System.out.println("ERRO: Atualização da Flag RecebidoEstoque [DB9:64.0] para TRUE");
                }

                //eventos.add("Seq " + seq++ + ": Adicionando bloco na posição: " + posicaoEstoque);
                byte offset = (byte) (68 + (estoquePlc.getPosicaoEstoque() - 1));

                try {
                    // Atualiza cor no CLP
                    plcConnector.writeByte(9, offset, (byte) estoquePlc.getCorGuardarEstoque());

                    // Cria mapa de dados com apenas uma posição
                    Map<String, Integer> dadosMap = new HashMap<>();
                    dadosMap.put("posicao:" + estoquePlc.getPosicaoEstoque(), estoquePlc.getCorGuardarEstoque());

                    // === Chama serviço de integração ===
                    //boolean sucesso = apiIntegrationService.salvarEstoque(dadosMap);
                    /* 
                    if (sucesso) {
                        System.out.println("Estoque salvo com sucesso na API.");
                    } else {
                        System.out.println("Falha ao salvar estoque na API.");
                        // aqui você poderia lançar uma exceção ou marcar para tentar novamente
                    }*/

                } catch (Exception e) {
                    System.out.println("ERRO: Na tentativa de adicionar no Estoque");
                    e.printStackTrace();
                }
        }
    }

    private void validarPedido(PlcConnector plcConnector) {
        if (estoquePlc.isIniciarPedido() == true && estoquePlc.isOcupado() == true) {
            try {
                plcConnector.writeBit(9, 62, 0, false); // coloca IniciarPedido em FALSE

            } catch (Exception e) {
                System.out.println(
                        "ERRO [iniciarPedido == true & ocupadoEst == true]: Atualização da Flag IniciarPedido [DB9:62.0] para FALSE");
            }
        }

    }

    private void validarOperacao(PlcConnector plcConnector) {
        if (estoquePlc.isStartOp() == false & estoquePlc.isFinishOp() == false & estoquePlc.isCancelOp() == false) {
            try {
                plcConnector.writeBit(9, 0, 0, false); // coloca RecebidoOPEst em FALSE

            } catch (Exception e) {
                System.out.println("ERRO: Atualização da Flag RecebidoOPEstoque [DB9:0.0] para FALSE");
            }
        }

        if (estoquePlc.isStartOp() == true & estoquePlc.isRecebidoOP() == false) {
            try {
                // System.out.println("StartOP: colocando RecebidoOPEstoque em TRUE");
                // eventos.add("Seq " + seq++ + ": coloca RecebidoOPEst em TRUE");
                plcConnector.writeBit(9, 0, 0, true); // coloca RecebidoOPEst em TRUE

            } catch (Exception e) {
                System.out.println(
                        "ERRO [startOp]: Atualização da Flag RecebidoOPEstoque [DB9:0.0] para TRUE");
            }
        }
        if (estoquePlc.isFinishOp() == true & estoquePlc.isRecebidoOP() == false) {
            try {
                // System.out.println("FinishOP: colocando RecebidoOPEstoque em TRUE");
                // eventos.add("Seq " + seq++ + ": coloca RecebidoOPEst em TRUE");
                plcConnector.writeBit(9, 0, 0, true); // coloca RecebidoOPEst em TRUE

            } catch (Exception e) {
                System.out.println(
                        "ERRO [finishOp]: Atualização da Flag RecebidoOPEstoque [DB9:0.0] para TRUE");
            }
        }
        if (estoquePlc.isRemoverEstoque() == false & estoquePlc.isAdicionarEstoque() == false) {

            try {
                plcConnector.writeBit(9, 64, 0, false); // coloca RecebidoEstoque em FALSE

            } catch (Exception e) {
                System.out.println("ERRO: Atualização da Flag RecebidoEstoque [DB9:64.0] para FALSE");
            }
        }
    }

}