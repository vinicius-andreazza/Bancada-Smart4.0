package com.smart.appsa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.appsa.model.enums.CorBloco;
import com.smart.appsa.service.EstoqueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/estoque")
public class EstoqueController {
    private final EstoqueService estoqueService;

    @GetMapping("/disponivel")
    public ResponseEntity<?> findDisponivel(){
        return ResponseEntity.ok(estoqueService.findByCor(CorBloco.NENHUM.getValue()));
    }
}
