package com.smart.appsa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Pedido;

import com.smart.appsa.model.Estoque;

import org.springframework.stereotype.Repository;
@Repository
public interface BlocoRepository extends JpaRepository<Bloco, Long> {
    List<Bloco> findByPedido(Pedido pedido);
    List<Bloco> findByPosEstoque(Estoque posEstoque);
}
