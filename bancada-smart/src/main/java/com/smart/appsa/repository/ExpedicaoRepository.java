package com.smart.appsa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smart.appsa.model.Expedicao;


@Repository
public interface ExpedicaoRepository extends JpaRepository<Expedicao, Long> {
    boolean existsByPosicao(Integer posicao);
    Optional<Expedicao> findFirstByPedidoIsNull();
    Optional<Expedicao> findByPosicao(Integer posicao);
}