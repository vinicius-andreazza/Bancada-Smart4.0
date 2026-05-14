package com.smart.appsa.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.appsa.dto.PedidoRequestDTO;
import com.smart.appsa.dto.PedidoResponseDTO;
import com.smart.appsa.service.PedidoService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    @GetMapping("")
    public ResponseEntity<List<PedidoResponseDTO>> findAll() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @PostMapping()
    public ResponseEntity<PedidoResponseDTO> create(@RequestBody PedidoRequestDTO pedidoRequestDTO){
        return ResponseEntity.status(201).body(pedidoService.criar(pedidoRequestDTO));
    }
    
}
