package com.smart.appsa.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.model.enums.CorBloco;
import com.smart.appsa.service.EstoqueService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/estoque")
public class EstoqueController {
    private final EstoqueService estoqueService;

    @GetMapping("/disponivel")
    public ResponseEntity<?> findDisponivel(){
        return ResponseEntity.ok(estoqueService.findByCor(CorBloco.NENHUM.getValue()));
    }

    @GetMapping("")
    public ResponseEntity<List<EstoqueDTO>> findAll(){
        return ResponseEntity.ok(estoqueService.findAll());
    }

    @PutMapping("/{pos}")
    public ResponseEntity<EstoqueDTO> updateEstoque(@PathVariable Integer pos,@RequestBody EstoqueDTO estoqueDTO){
        return ResponseEntity.ok(estoqueService.put(pos, estoqueDTO));
    }

    @PutMapping("")
    public ResponseEntity<List<EstoqueDTO>> updateAllEstoque(@RequestBody List<EstoqueDTO> estoqueDTO){
        return ResponseEntity.ok(estoqueService.putAll(estoqueDTO));
    }

    
}
