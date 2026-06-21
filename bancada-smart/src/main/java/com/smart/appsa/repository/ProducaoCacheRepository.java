package com.smart.appsa.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.smart.appsa.dto.producao.ProducaoSnapshot;

import tools.jackson.databind.ObjectMapper;

@Repository
public class ProducaoCacheRepository {

    private static final String PREFIXO_CHAVE = "producao:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public ProducaoCacheRepository(RedisTemplate<String, String> redisTemplate, ObjectMapper producaoObjectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = producaoObjectMapper;
    }

    private String chave(int codPedido) {
        return PREFIXO_CHAVE + codPedido;
    }

    public void salvar(int codPedido, ProducaoSnapshot snapshot) {
        String json = objectMapper.writeValueAsString(snapshot);
        redisTemplate.opsForValue().set(chave(codPedido), json);

        String confirmacao = redisTemplate.opsForValue().get(chave(codPedido));
        if (confirmacao == null) {
            throw new IllegalStateException(
                    "Falha ao confirmar gravação do snapshot no Redis para o pedido " + codPedido);
        }
    }

    public ProducaoSnapshot buscar(int codPedido) {
        String json = redisTemplate.opsForValue().get(chave(codPedido));
        if (json == null) {
            return null;
        }
        return objectMapper.readValue(json, ProducaoSnapshot.class);
    }

    public void remover(int codPedido) {
        redisTemplate.delete(chave(codPedido));
    }

    public boolean existe(int codPedido) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(chave(codPedido)));
    }
}