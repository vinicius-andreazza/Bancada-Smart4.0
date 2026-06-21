package com.smart.appsa.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.smart.appsa.config.ipconfig.SeletorTampaIp;
import com.smart.appsa.exception.SeletorTampaException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeletorTampaService {
    private final SeletorTampaIp seletorTampaIp;

    public void updateTampa(int tampa) {
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
}
