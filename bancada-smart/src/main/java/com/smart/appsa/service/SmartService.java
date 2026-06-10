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
import com.smart.appsa.dto.PedidoRequestDTO;
import com.smart.appsa.exception.SeletorTampaException;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.model.Pedido;
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
        /*PlcConnector connector = plcConnectionService.getConnection(pedidoRequest.clpIp());

        if (connector != null) {
            try {
                // 3. Escrever bloco de bytes no CLP (ex: a partir da DB19, offset 2)
                connector.writeBlock(9, 2, 60, buffer);
                System.out.println("Dados enviados para o CLP: " + pedidoRequest.clpIp());

                atualizarTampa(pedido.getCorTampa().getValue());

                iniciarExecucaoPedido(pedidoRequest.clpIp());

            } catch (Exception ex) {
                System.err.println("Erro ao enviar dados para o CLP: " + ex.getMessage());
            }
        }*/
    }

    private void escreverBloco(Bloco bloco, ByteBuffer buffer) {
        buffer.putShort((short) bloco.getCorBloco().getValue()); // Cor_Andar
        buffer.putShort((short) bloco.getPosEstoque().intValue()); // Posicao_Estoque_Andar

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
        ByteBuffer buffer = ByteBuffer.allocate(TOTAL_BYTES);

        List<Bloco> blocos = pedido.getBlocos();

        for (int i = 0; i < 3; i++) {
            if (i < blocos.size()) {
                escreverBloco(blocos.get(i), buffer);
            } else {
                escreverBlocoVazio(buffer);
            }
        }

        // Offset 56
        buffer.putShort((short) pedido.getCodPedido().intValue()); // Numero_Pedido
        buffer.putShort((short) pedido.getTipoPedido().getValue()); // Andares
        buffer.putShort((short) pedido.getPosExpedicao().intValue()); // Posicao_Expedicao

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

    private void atualizarTampa(int tampa) {
        System.out.println("\n\nSELETOR DE TAMPAS INSTALADO NA BANCADA\n\n");
        // Passo 2) Selecionar a tampa via POST
        try {
            RestTemplate apiSeletorTampa = new RestTemplate();
            String url = "http://10.74.241.245/api/move_pos";

            // 1. Definir o cabeçalho como application/x-www-form-urlencoded
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 2. Usar MultiValueMap (específico para formulários no Spring)
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("pos", String.valueOf(tampa));
            map.add("offset", "0");

            // 3. Criar a entidade com cabeçalhos e corpo
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            // 4. Tente ler a resposta primeiro como String para ver o que o ESP32 está
            // realmente enviando
            ResponseEntity<String> rawResponse = apiSeletorTampa.postForEntity(url, request, String.class);
            System.out.println("Resposta Bruta do ESP32: " + rawResponse.getBody());

            // 5. Agora, para a sua lógica de negócio, usamos o Map
            ResponseEntity<Map> response = apiSeletorTampa.postForEntity(url, request, Map.class);
            Map<String, Object> body = response.getBody();

            // Verificação robusta
            if (body == null || body.get("status") == null) {
                throw new SeletorTampaException("Resposta invalida do seletor tampa");
            }

            String status = body.get("status").toString();

            // Verificação flexível (ignora maiúsculas/minúsculas)
            if (!status.toLowerCase().contains("ok")) {
                throw new SeletorTampaException("Erro status: "+status);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new SeletorTampaException("Erro seletor de tampa");
        }
    }
}