package com.smart.appsa.service;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.ipconfig.EstoqueIp;
import com.smart.appsa.config.ipconfig.SeletorTampaIp;
import com.smart.appsa.dto.PedidoRequestDTO;
import com.smart.appsa.exception.SeletorTampaException;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmartService {

    private final PedidoService pedidoService;

    private final PlcConnectionService plcConnectionService;

    private final PedidoRepository pedidoRepository;

    private final BlocoService blocoService;

    private final SeletorTampaIp seletorTampaIp;

    private final EstoqueIp estoqueIp;

    private static final int TOTAL_SHORTS = 30;
    private static final int TOTAL_BYTES = TOTAL_SHORTS * 2;

    public void enviarParaProducao(PedidoRequestDTO pedidoRequest) {
        Pedido pedido = setPedido(pedidoRequest.codPedido());

        byte[] buffer = converterParaBytes(pedido);

        printHex(buffer);

        writeDataInPlc(pedido, buffer);

        updatePedido(pedido);
    }

    private Pedido setPedido(int codPedido) {
        Pedido pedido = pedidoRepository.findByCodPedido(codPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe"));

        pedido.getBlocos().forEach(b -> blocoService.assignEstoquePosition(b));

        pedidoService.assignPosPedidoInExpedicao(pedido);
        return pedido;
    }

    private byte[] converterParaBytes(Pedido pedido) {
        ByteBuffer buffer = ByteBuffer.allocate(TOTAL_BYTES);

        List<Bloco> blocos = pedido.getBlocos();

        for (int i = 0; i < 3; i++) {
            if (i < blocos.size()) {
                escreverBloco(blocos.get(i), buffer);
            } else {
                escreverBlocoVazio(buffer);
            }
        }

        buffer.putShort((short) pedido.getCodPedido().intValue());
        buffer.putShort((short) pedido.getTipoPedido().getValue());
        buffer.putShort((short) pedido.getPosExpedicao().intValue());

        return buffer.array();
    }

    private void escreverBloco(Bloco bloco, ByteBuffer buffer) {
        buffer.putShort((short) bloco.getCorBloco().getValue());
        buffer.putShort((short) bloco.getPosEstoque().intValue());

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

        if (connector != null) {
            try {
                connector.writeBlock(9, 2, 60, buffer);
                System.out.println("Dados enviados para o CLP: " + estoqueIp.getIp());

                atualizarTampa(pedido.getCorTampa().getValue());

                iniciarExecucaoPedido(estoqueIp.getIp());

            } catch (Exception ex) {
                System.err.println("Erro ao enviar dados para o CLP: " + ex.getMessage());
            }
        }
    }

    private void atualizarTampa(int tampa) {
        if (seletorTampaIp.getEndpointApi() == null) {
            return;
        }
        System.out.println("\n\nSELETOR DE TAMPAS INSTALADO NA BANCADA\n\n");
        try {
            HttpHeaders headers = new HttpHeaders();
            setHeaders(headers);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            setBody(map, tampa);

            Map<String, Object> body = sendRequest(headers, map);

            verifyResponse(body);

        } catch (Exception e) {
            e.printStackTrace();
            throw new SeletorTampaException("Erro seletor de tampa");
        }
    }

    private void setHeaders(HttpHeaders headers) {
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    private void setBody(MultiValueMap<String, String> map, int tampa) {
        map.add("pos", String.valueOf(tampa));
        map.add("offset", "0");
    }

    private Map<String, Object> sendRequest(HttpHeaders headers, MultiValueMap<String, String> map) {
        RestTemplate apiSeletorTampa = new RestTemplate();
        String url = seletorTampaIp.getEndpointApi();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> rawResponse = apiSeletorTampa.postForEntity(url, request, String.class);
        System.out.println("Resposta Bruta do ESP32: " + rawResponse.getBody());

        ResponseEntity<Map> response = apiSeletorTampa.postForEntity(url, request, Map.class);

        return response.getBody();
    }

    private void verifyResponse(Map<String, Object> body) {
        if (body == null || body.get("status") == null) {
            throw new SeletorTampaException("Resposta invalida do seletor tampa");
        }

        String status = body.get("status").toString();

        if (!status.toLowerCase().contains("ok")) {
            throw new SeletorTampaException("Erro status: " + status);
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

        }
    }

    private void resetFlags(String ipClp) throws Exception {
        PlcConnector plcConnector = plcConnectionService.getConnection(ipClp);
        plcConnector.writeBit(9, 0, 0, Boolean.parseBoolean("FALSE"));
        plcConnector.writeBit(9, 64, 0, Boolean.parseBoolean("FALSE"));
        plcConnector.writeBit(9, 64, 1, Boolean.parseBoolean("FALSE"));
        plcConnector.writeBit(9, 62, 0, Boolean.parseBoolean("FALSE"));
    }

    private void updatePedido(Pedido pedido) {
        pedido.setStatus(StatusPedido.PRODUCAO);

        pedidoRepository.save(pedido);
    }

}