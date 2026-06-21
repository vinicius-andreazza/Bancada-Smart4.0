package com.smart.appsa.service;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.ipconfig.EstoqueIp;
import com.smart.appsa.dto.PedidoRequestDTO;
import com.smart.appsa.exception.SeletorTampaException;
import com.smart.appsa.mapper.clp.ProducaoPlcMapper;
import com.smart.appsa.model.Pedido;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmartService {

    private final SeletorTampaService seletorTampaService;

    private final PlcConnectionService plcConnectionService;

    private final ProducaoService producaoService;

    private final EstoqueIp estoqueIp;


    public void enviarParaProducao(PedidoRequestDTO pedidoRequest) {
        Pedido pedido = producaoService.iniciarProducao(pedidoRequest);

        byte[] buffer = ProducaoPlcMapper.mapToBytes(pedido);

        printHex(buffer);

        try {
            writeDataInPlc(pedido, buffer);
        } catch (RuntimeException e) {
            producaoService.cancelarOuFalharProducao(pedido.getCodPedido(),
                    "falha no envio à bancada: " + e.getMessage());
            throw e;
        }
    }
    
    public void printHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        System.out.println("--- BLOCO DE BYTES (HEXADECIMAL) ---");

        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02X ", bytes[i]));

            if ((i + 1) % 10 == 0) {
                sb.append("\n");
            }
        }

        System.out.println(sb.toString());
        System.out.println("------------------------------------");
    }

    private void writeDataInPlc(Pedido pedido, byte[] buffer) {
        PlcConnector connector = plcConnectionService.getConnection(estoqueIp.getIp());

        if (connector == null) {
            throw new IllegalStateException("Sem conexão com o CLP de estoque: " + estoqueIp.getIp());
        }

        try {
            connector.writeBlock(9, 2, 60, buffer);
            System.out.println("Dados enviados para o CLP: " + estoqueIp.getIp());

            seletorTampaService.updateTampa(pedido.getCorTampa().getValue());

            iniciarExecucaoPedido(estoqueIp.getIp());

        } catch (SeletorTampaException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao enviar dados para o CLP: " + ex.getMessage(), ex);
        }
    }

    

    public void iniciarExecucaoPedido(String ipClp) {
        PlcConnector plcConnector = plcConnectionService.getConnection(ipClp);
        if (plcConnector == null) {
            return;
        }

        try {

            resetFlags(ipClp);

            System.out.println("SETAR FLAG INICIAR PEDIDO");
            plcConnector.writeBit(9, 62, 0, Boolean.parseBoolean("TRUE"));

            Thread.sleep(800);

            System.out.println("RESETAR FLAG INICIAR PEDIDO");
            plcConnector.writeBit(9, 62, 0, Boolean.parseBoolean("FALSE"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void resetFlags(String ipClp) throws Exception {
        PlcConnector plcConnector = plcConnectionService.getConnection(ipClp);
        plcConnector.writeBit(9, 0, 0, Boolean.parseBoolean("FALSE"));
        plcConnector.writeBit(9, 64, 0, Boolean.parseBoolean("FALSE"));
        plcConnector.writeBit(9, 64, 1, Boolean.parseBoolean("FALSE"));
        plcConnector.writeBit(9, 62, 0, Boolean.parseBoolean("FALSE"));
    }

}