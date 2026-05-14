package com.smart.appsa.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByCodPedido(String codPedido);
 
    List<Pedido> findByStatus(StatusPedido status);
 
    List<Pedido> findByTipoPedido(TipoPedido tipoPedido);
 
    List<Pedido> findByCorTampa(CorTampa corTampa);
 
    List<Pedido> findByExpedicao(Expedicao expedicao);
 
    List<Pedido> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);
}
