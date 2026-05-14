package com.smart.appsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smart.appsa.model.Estoque;
import java.util.List;
import java.util.Optional;



@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    boolean existsByPosicao(Integer posicao);
    int countByCor(Integer cor);
    Optional<Estoque> findByPosicao(Integer posicao);
    Optional<Estoque> findFirstByCor(Integer cor);
}