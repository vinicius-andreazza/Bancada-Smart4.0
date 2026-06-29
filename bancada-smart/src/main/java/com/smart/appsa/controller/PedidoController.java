package com.smart.appsa.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.CountStatus;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.service.PedidoService;
import com.smart.appsa.service.ProducaoService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final ProducaoService producaoService;
    private final PedidoService pedidoService;

    @GetMapping("")
    public ResponseEntity<Page<PedidoResponseDTO>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.findAll(pageable));
    }

    @GetMapping("/pendente")
    public ResponseEntity<Page<PedidoResponseDTO>> findPendente(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.findPendente(pageable));
    }

    @GetMapping("/producao")
    public ResponseEntity<Page<PedidoResponseDTO>> findProducao(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.findProducao(pageable));
    }

    @GetMapping("/concluido")
    public ResponseEntity<Page<PedidoResponseDTO>> findConcluido(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.findConcluido(pageable));
    }

    @GetMapping("/cancelado")
    public ResponseEntity<Page<PedidoResponseDTO>> findCancelado(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.findCancelado(pageable));
    }

    @GetMapping("/fila")
    public ResponseEntity<List<PedidoResponseDTO>> findFila() {
        return ResponseEntity.ok(producaoService.findPedidosInProducao());
    }

    @GetMapping("/contagens")
    public ResponseEntity<CountStatus> countStatus() {
        return ResponseEntity.ok(pedidoService.countStatus());
    }

    @GetMapping("/ultimo")
    public ResponseEntity<PedidoResponseDTO> getLatestPedidoConcluido() {
        return ResponseEntity.ok(pedidoService.findLatestConcluido());
    }

    @GetMapping("/emProducao")
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosInProducao() {
        return ResponseEntity.ok(producaoService.findPedidosInProducao());
    }

    @PostMapping()
    public ResponseEntity<PedidoResponseDTO> create(@RequestBody PedidoRequestDTO pedidoRequestDTO) {
        System.out.println(pedidoRequestDTO);
        return ResponseEntity.status(201).body(pedidoService.create(pedidoRequestDTO));
    }

    @PostMapping("/enviar")
    public ResponseEntity<String> sendPedido(@RequestBody PedidoRequestDTO pedidoRequestDTO) {
        producaoService.adicionarPedido(pedidoRequestDTO);
        return ResponseEntity.ok("Pedido adicionado à fila de produção");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> updateToConcluido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.updateToConcluido(id));
    }

    @DeleteMapping("/{id}/fila")
    public ResponseEntity<PedidoResponseDTO> removeDaFila(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.removeDaFila(id));
    }

}
