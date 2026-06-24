package com.smart.appsa.repository.cache;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.smart.appsa.dto.producao.ProducaoSnapshot;

import tools.jackson.databind.ObjectMapper;

@Repository
public class CacheRepository {

      private static final String PREFIXO_ORDEM = "producao:ordem";
    private static final String PREFIXO_PEDIDOS = "producao:pedidos";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public CacheRepository(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper producaoObjectMapper) {

        this.redisTemplate = redisTemplate;
        this.objectMapper = producaoObjectMapper;
    }

    public void salvar(int codPedido, ProducaoSnapshot snapshot) {

        String json = objectMapper.writeValueAsString(snapshot);

        redisTemplate.opsForHash()
                .put(PREFIXO_PEDIDOS, String.valueOf(codPedido), json);

        redisTemplate.opsForZSet()
                .add(
                        PREFIXO_ORDEM,
                        String.valueOf(codPedido),
                        Instant.now().toEpochMilli());

        Boolean existe = redisTemplate.opsForHash()
                .hasKey(PREFIXO_PEDIDOS, String.valueOf(codPedido));

        if (!Boolean.TRUE.equals(existe)) {
            throw new IllegalStateException(
                    "Falha ao confirmar gravação do snapshot no Redis para o pedido "
                            + codPedido);
        }
    }

    public ProducaoSnapshot buscar(int codPedido) {

        Object json = redisTemplate.opsForHash()
                .get(PREFIXO_PEDIDOS, String.valueOf(codPedido));

        if (json == null) {
            return null;
        }

        return objectMapper.readValue(
                json.toString(),
                ProducaoSnapshot.class);
    }

    public ProducaoSnapshot buscarMaisAntigo() {

        Set<String> pedidos = redisTemplate.opsForZSet()
                .range(PREFIXO_ORDEM, 0, 0);

        if (pedidos == null || pedidos.isEmpty()) {
            return null;
        }

        String codPedido = pedidos.iterator().next();

        return buscar(Integer.parseInt(codPedido));
    }

    public Integer buscarCodigoMaisAntigo() {

        Set<String> pedidos = redisTemplate.opsForZSet()
                .range(PREFIXO_ORDEM, 0, 0);

        if (pedidos == null || pedidos.isEmpty()) {
            return null;
        }

        return Integer.parseInt(pedidos.iterator().next());
    }

    public void remover(int codPedido) {

        String pedido = String.valueOf(codPedido);

        redisTemplate.opsForHash()
                .delete(PREFIXO_PEDIDOS, pedido);

        redisTemplate.opsForZSet()
                .remove(PREFIXO_ORDEM, pedido);
    }

    public List<ProducaoSnapshot> buscarTodos() {

    Set<String> codigos = redisTemplate.opsForZSet()
            .range(PREFIXO_ORDEM, 0, -1);

    if (codigos == null || codigos.isEmpty()) {
        return List.of();
    }

    List<ProducaoSnapshot> snapshots = new ArrayList<>();

    for (String codigo : codigos) {

        Object json = redisTemplate.opsForHash()
                .get(PREFIXO_PEDIDOS, codigo);

        if (json == null) {
            continue;
        }

        snapshots.add(
                objectMapper.readValue(
                        json.toString(),
                        ProducaoSnapshot.class));
    }

    return snapshots;
}

    public boolean existe(int codPedido) {

        return Boolean.TRUE.equals(
                redisTemplate.opsForHash()
                        .hasKey(PREFIXO_PEDIDOS, String.valueOf(codPedido)));
    }

}