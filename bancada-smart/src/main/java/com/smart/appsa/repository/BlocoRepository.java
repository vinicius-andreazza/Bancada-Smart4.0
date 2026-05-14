package com.smart.appsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Pedido;

import java.util.List;
import com.smart.appsa.model.Estoque;



public interface BlocoRepository extends JpaRepository<Bloco, Long> {
    List<Bloco> findByPedido(Pedido pedido);
    List<Bloco> findByPosEstoque(Estoque posEstoque);
}
