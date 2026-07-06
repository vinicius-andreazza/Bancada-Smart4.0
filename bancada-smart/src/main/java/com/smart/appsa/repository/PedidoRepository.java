package com.smart.appsa.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Integer countByStatus(StatusPedido status);

    @Query(value = """
            SELECT
            CASE
                WHEN NOT EXISTS (SELECT 1 FROM pedido WHERE cod_pedido = 1)
                    THEN 1
                ELSE (
                    SELECT MIN(p1.cod_pedido + 1)
                    FROM pedido p1
                    LEFT JOIN pedido p2
                        ON p2.cod_pedido = p1.cod_pedido + 1
                    WHERE p2.cod_pedido IS NULL
                )
            END;
                    """, nativeQuery = true)
    Integer findNextAvailableCodPedido();

    boolean existsByCodPedido(Integer codPedido);

    Optional<Pedido> findByCodPedido(Integer codPedido);

    Optional<Pedido> findFirstByStatusOrderByDataEntradaDesc(StatusPedido status);

    List<Pedido> findByStatus(StatusPedido status);

    Page<Pedido> findByStatus(StatusPedido status, Pageable pageable);

    List<Pedido> findByTipoPedido(TipoPedido tipoPedido);

    List<Pedido> findByCorTampa(CorTampa corTampa);

    List<Pedido> findByPosExpedicao(Expedicao expedicao);

    List<Pedido> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Pedido> findByStatusNotOrderByPosExpedicaoAsc(StatusPedido statusPedido);
}
