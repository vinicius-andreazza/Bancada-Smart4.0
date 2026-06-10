package com.smart.appsa.service;

import java.nio.ByteBuffer;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.dto.PedidoRequestDTO;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.repository.BlocoRepository;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmartService {

    private final PlcConnectionService plcConnectionService;

    private final PedidoRepository pedidoRepository;

    private final BlocoService blocoService;

    private static final int TOTAL_SHORTS = 30;
    private static final int TOTAL_BYTES = TOTAL_SHORTS * 2;

    public void enviarParaProducao(PedidoRequestDTO pedidoRequest) {
        Pedido pedido = pedidoRepository.findByCodPedido(pedidoRequest.codPedido())
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe"));

        byte[] buffer = converterParaBytes(pedido);

        pedido.getBlocos().forEach(b -> blocoService.assignEstoquePosition(b));

        printHex(buffer);
        // 2. Obter a conexão única via seu Service
        PlcConnector connector = plcConnectionService.getConnection(pedidoRequest.clpIp());

        if (connector != null) {
            try {
                // 3. Escrever bloco de bytes no CLP (ex: a partir da DB19, offset 2)
                connector.writeBlock(9, 2, 60, buffer);
                System.out.println("Dados enviados para o CLP: " + pedidoRequest.clpIp());

                iniciarExecucaoPedido(pedidoRequest.clpIp());

            } catch (Exception ex) {
                System.err.println("Erro ao enviar dados para o CLP: " + ex.getMessage());
            }
        }
    }

    private void escreverBloco(Bloco bloco, ByteBuffer buffer) {
        buffer.putShort((short) bloco.getCorBloco().getValue());       // Cor_Andar
        buffer.putShort((short) bloco.getPosEstoque().intValue());     // Posicao_Estoque_Andar
    
        List<Lamina> laminas = bloco.getLaminas();
    
        Lamina[] slots = new Lamina[3];
        for (Lamina lamina : laminas) {
            int pos = lamina.getPosicaoLamina().getValue();
            slots[pos - 1] = lamina;
        }

        for (Lamina slot : slots) {
            buffer.putShort(slot != null ? (short) slot.getCorLamina().getValue() : (short) 0);
        }

        for (Lamina slot : slots) {
            buffer.putShort(slot != null ? (short) slot.getPadraoLamina().getValue() : (short) 0);
        }
        
        buffer.putShort((short) 0);
    }
    
    private void escreverBlocoVazio(ByteBuffer buffer) {
        for (int i = 0; i < 9; i++) {
            buffer.putShort((short) 0);
        }
    }
    
    private byte[] converterParaBytes(Pedido pedido) {
        ByteBuffer buffer = ByteBuffer.allocate(60);
    
        List<Bloco> blocos = pedido.getBlocos();
    
        for (int i = 0; i < 3; i++) {
            if (i < blocos.size()) {
                escreverBloco(blocos.get(i), buffer);
            } else {
                escreverBlocoVazio(buffer);
            }
        }
    
        // Offset 56
        buffer.putShort((short) pedido.getCodPedido().intValue());  // Numero_Pedido
        buffer.putShort((short) pedido.getTipoPedido().getValue());             // Andares
        buffer.putShort((short) pedido.getPosExpedicao().intValue());           // Posicao_Expedicao
    
        return buffer.array();
    }
    // Printar bloco de bytes do Pedido no console
    public void printHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        System.out.println("--- BLOCO DE BYTES (HEXADECIMAL) ---");

        for (int i = 0; i < bytes.length; i++) {
            // Converte o byte para Hex e garante que tenha 2 dígitos (ex: 0A em vez de A)
            sb.append(String.format("%02X ", bytes[i]));

            // Opcional: Quebra de linha a cada 10 bytes para facilitar a leitura
            if ((i + 1) % 10 == 0) {
                sb.append("\n");
            }
        }

        System.out.println(sb.toString());
        System.out.println("------------------------------------");
    }

    // Envia comando para a Planta Smart iniciar a produção do Pedido
    public void iniciarExecucaoPedido(String ipClp) {
        PlcConnector plcConnector = plcConnectionService.getConnection(ipClp);
        if (plcConnector == null) {
            return;
        }

        try {

            // Inicializa as flags da estação ESTOQUE
            // plcConnector.connect();
            plcConnector.writeBit(9, 0, 0, Boolean.parseBoolean("FALSE"));
            plcConnector.writeBit(9, 64, 0, Boolean.parseBoolean("FALSE"));
            plcConnector.writeBit(9, 64, 1, Boolean.parseBoolean("FALSE"));
            plcConnector.writeBit(9, 62, 0, Boolean.parseBoolean("FALSE"));

            // plcConnector.writeBit(9, 62, 0, Boolean.parseBoolean("FALSE"));
            // Iniciar pedido
            System.out.println("SETAR FLAG INICIAR PEDIDO");
            plcConnector.writeBit(9, 62, 0, Boolean.parseBoolean("TRUE"));

            Thread.sleep(800);

            System.out.println("RESETAR FLAG INICIAR PEDIDO");
            plcConnector.writeBit(9, 62, 0, Boolean.parseBoolean("FALSE"));

        } catch (Exception ex) {

        }
    }
}