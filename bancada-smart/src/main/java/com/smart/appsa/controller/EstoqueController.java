package com.smart.appsa.controller;


import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.model.enums.CorBloco;
import com.smart.appsa.service.EstoqueService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/estoque")
@Slf4j
@Tag(name = "Estoque", description = "Leitura e escrita do estado das posições do estoque de blocos")
public class EstoqueController {
    private final EstoqueService estoqueService;

    @Operation(summary = "Listar posições disponíveis", description = "Retorna as posições do estoque com cor = NENHUM (posições livres/vazias)")
    @ApiResponse(responseCode = "200", description = "Posições disponíveis retornadas")
    @GetMapping("/disponivel")
    public ResponseEntity<?> findDisponivel(){
        return ResponseEntity.ok(estoqueService.findByCor(CorBloco.NENHUM.getValue()));
    }

    @Operation(summary = "Listar todo o estoque", description = "Retorna todas as 28 posições do estoque com a cor do bloco em cada posição")
    @ApiResponse(responseCode = "200", description = "Lista completa do estoque")
    @GetMapping("")
    public ResponseEntity<List<EstoqueDTO>> findAll(){
        return ResponseEntity.ok(estoqueService.findAll());
    }

    @Operation(summary = "Stream SSE do estoque", description = "Abre um Server-Sent Events stream com atualizações em tempo real do estado do estoque. Não testável diretamente pelo Swagger UI.")
    @ApiResponse(responseCode = "200", description = "Stream SSE aberto",
        content = @Content(mediaType = "text/event-stream", schema = @Schema(type = "string", description = "Fluxo SSE — use EventSource no cliente")))
    @GetMapping(value = "/read", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter readAll() {
        log.info("SSE aberto com Estoque");
        return estoqueService.conectar();
    }

    @Operation(summary = "Atualizar uma posição do estoque")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Posição atualizada"),
        @ApiResponse(responseCode = "409", description = "CLPs não estão em comunicação")
    })
    @PutMapping("/{pos}")
    public ResponseEntity<EstoqueDTO> updateEstoque(
            @Parameter(description = "Posição no estoque (1–28)", required = true) @PathVariable Integer pos,
            @RequestBody EstoqueDTO estoqueDTO){
        return ResponseEntity.ok(estoqueService.put(pos, estoqueDTO));
    }

    @Operation(summary = "Atualizar todo o estoque em lote")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estoque atualizado em lote"),
        @ApiResponse(responseCode = "409", description = "CLPs não estão em comunicação")
    })
    @PutMapping("")
    public ResponseEntity<List<EstoqueDTO>> updateAllEstoque(@RequestBody List<EstoqueDTO> estoqueDTO){
        log.info("DTO recebido: {}",estoqueDTO);
        return ResponseEntity.ok(estoqueService.putAll(estoqueDTO));
    }

    
}
