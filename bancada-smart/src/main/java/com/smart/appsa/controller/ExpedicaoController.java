package com.smart.appsa.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.smart.appsa.dto.ExpedicaoDTO;
import com.smart.appsa.service.ExpedicaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expedicao")
@Slf4j
@Tag(name = "Expedição", description = "Leitura dos slots de expedição da bancada")
public class ExpedicaoController {
    private final ExpedicaoService expedicaoService;

    @Operation(summary = "Listar todas as posições de expedição", description = "Retorna as 12 posições de expedição com o código do pedido alocado em cada uma (null = posição livre)")
    @ApiResponse(responseCode = "200", description = "Lista de posições de expedição")
    @GetMapping("")
    public ResponseEntity<List<ExpedicaoDTO>> findAll(){
        return ResponseEntity.ok(expedicaoService.findAll());
    }

    @Operation(summary = "Stream SSE da expedição", description = "Abre um Server-Sent Events stream com atualizações em tempo real do estado da expedição. Não testável diretamente pelo Swagger UI.")
    @ApiResponse(responseCode = "200", description = "Stream SSE aberto",
        content = @Content(mediaType = "text/event-stream", schema = @Schema(type = "string", description = "Fluxo SSE — use EventSource no cliente")))
    @GetMapping(value = "/read", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter readAll() {
        log.info("SSE aberto com Expedição");
        return expedicaoService.conectar();
    }

}
