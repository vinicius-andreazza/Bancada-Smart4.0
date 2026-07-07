package com.smart.appsa.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.CountStatus;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.service.PedidoService;
import com.smart.appsa.service.ProducaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "CRUD e ciclo de vida dos pedidos de produção")
public class PedidoController {
    private final ProducaoService producaoService;
    private final PedidoService pedidoService;

    @Operation(summary = "Listar todos os pedidos (paginado)")
    @ApiResponse(responseCode = "200", description = "Página de pedidos retornada")
    @GetMapping("")
    public ResponseEntity<Page<PedidoResponseDTO>> findAll(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.findAll(pageable));
    }

    @Operation(summary = "Listar pedidos pendentes (paginado)")
    @ApiResponse(responseCode = "200", description = "Página de pedidos pendentes")
    @GetMapping("/pendente")
    public ResponseEntity<Page<PedidoResponseDTO>> findPendente(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.findPendente(pageable));
    }

    @Operation(summary = "Listar pedidos em produção (paginado)")
    @ApiResponse(responseCode = "200", description = "Página de pedidos em produção")
    @GetMapping("/producao")
    public ResponseEntity<Page<PedidoResponseDTO>> findProducao(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.findProducao(pageable));
    }

    @Operation(summary = "Listar pedidos concluídos (paginado)")
    @ApiResponse(responseCode = "200", description = "Página de pedidos concluídos")
    @GetMapping("/concluido")
    public ResponseEntity<Page<PedidoResponseDTO>> findConcluido(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.findConcluido(pageable));
    }

    @Operation(summary = "Listar pedidos cancelados (paginado)")
    @ApiResponse(responseCode = "200", description = "Página de pedidos cancelados")
    @GetMapping("/cancelado")
    public ResponseEntity<Page<PedidoResponseDTO>> findCancelado(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.findCancelado(pageable));
    }

    @Operation(summary = "Fila de produção ativa", description = "Retorna os pedidos que estão atualmente na fila de execução da bancada")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos na fila")
    @GetMapping("/fila")
    public ResponseEntity<List<PedidoResponseDTO>> findFila() {
        return ResponseEntity.ok(producaoService.findPedidosInProducao());
    }

    @Operation(summary = "Contagens por status", description = "Retorna contadores agrupados por status: total, pendentes, producao, concluidos, cancelado")
    @ApiResponse(responseCode = "200", description = "Contagens retornadas")
    @GetMapping("/contagens")
    public ResponseEntity<CountStatus> countStatus() {
        return ResponseEntity.ok(pedidoService.countStatus());
    }

    @Operation(summary = "Último pedido concluído")
    @ApiResponse(responseCode = "200", description = "Pedido mais recente com status CONCLUIDO")
    @GetMapping("/ultimo")
    public ResponseEntity<PedidoResponseDTO> getLatestPedidoConcluido() {
        return ResponseEntity.ok(pedidoService.findLatestConcluido());
    }

    @Operation(summary = "Pedidos em produção (equivalente a /fila)")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos em produção")
    @GetMapping("/emProducao")
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosInProducao() {
        return ResponseEntity.ok(producaoService.findPedidosInProducao());
    }

    @Operation(summary = "Criar pedido", description = "Cria um novo pedido no banco de dados. Requer CLPs em comunicação.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        @ApiResponse(responseCode = "409", description = "CLPs não estão em comunicação — chame POST /api/smart/start primeiro")
    })
    @PostMapping()
    public ResponseEntity<PedidoResponseDTO> create(@RequestBody PedidoRequestDTO pedidoRequestDTO) {
        return ResponseEntity.status(201).body(pedidoService.create(pedidoRequestDTO));
    }

    @Operation(summary = "Enviar pedido para a bancada", description = "Adiciona o pedido na fila de produção e dispara o envio para os CLPs. Requer CLPs em comunicação.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido enfileirado para produção"),
        @ApiResponse(responseCode = "409", description = "CLPs não estão em comunicação")
    })
    @PostMapping("/enviar")
    public ResponseEntity<String> sendPedido(@RequestBody PedidoRequestDTO pedidoRequestDTO) {
        producaoService.adicionarPedido(pedidoRequestDTO);
        return ResponseEntity.ok("Pedido enviado");
    }

    @Operation(summary = "Atualizar status do pedido para CONCLUIDO")
    @ApiResponse(responseCode = "200", description = "Pedido atualizado")
    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> updateToConcluido(
            @Parameter(description = "ID do pedido", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.updateToConcluido(id));
    }

    @Operation(summary = "Atualizar parcialmente um pedido (PATCH)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido atualizado"),
        @ApiResponse(responseCode = "409", description = "CLPs não estão em comunicação")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> patchPedido(
            @Parameter(description = "ID do pedido", required = true) @PathVariable Long id,
            @RequestBody PedidoRequestDTO pedidoRequestDTO) {
        return ResponseEntity.ok(pedidoService.patch(id, pedidoRequestDTO));
    }

    @Operation(summary = "Remover pedido da fila de produção")
    @ApiResponse(responseCode = "200", description = "Pedido removido da fila")
    @DeleteMapping("/{id}/fila")
    public ResponseEntity<PedidoResponseDTO> removeDaFila(
            @Parameter(description = "ID do pedido", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.removeDaFila(id));
    }

    @Operation(summary = "Recriar pedido (remake)", description = "Cria um novo pedido baseado na configuração de um pedido anterior identificado pelo codPedido")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido recriado com sucesso"),
        @ApiResponse(responseCode = "409", description = "CLPs não estão em comunicação")
    })
    @PostMapping("/{codPedido}/remake")
    public ResponseEntity<PedidoResponseDTO> remake(
            @Parameter(description = "Código do pedido original", required = true) @PathVariable Integer codPedido) {
        return ResponseEntity.status(201).body(pedidoService.remake(codPedido));
    }

}
