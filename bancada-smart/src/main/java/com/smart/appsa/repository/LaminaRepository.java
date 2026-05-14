package com.smart.appsa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;

@Repository
public interface LaminaRepository extends JpaRepository<Lamina, Long> {
    List<Lamina> findByBloco(Bloco bloco);
}