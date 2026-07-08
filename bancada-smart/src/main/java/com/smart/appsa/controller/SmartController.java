package com.smart.appsa.controller;

import com.smart.appsa.config.ipconfig.EstoqueIp;
import com.smart.appsa.config.ipconfig.ExpedicaoIp;
import com.smart.appsa.config.ipconfig.MontagemIp;
import com.smart.appsa.config.ipconfig.ProcessoIp;
import com.smart.appsa.config.ipconfig.SeletorTampaIp;
import com.smart.appsa.dto.CommDto;
import com.smart.appsa.service.clp.estacao.EstoqueComm;
import com.smart.appsa.service.clp.estacao.ExpedicaoComm;
import com.smart.appsa.service.clp.estacao.MontagemComm;
import com.smart.appsa.service.clp.estacao.ProcessoComm;
import com.smart.appsa.service.sse.MonitoramentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/smart")
@Slf4j
@Tag(name = "Comunicação CLP", description = "Gerencia a conexão com os CLPs/PLCs das estações da bancada")
public class SmartController {

    private final ExpedicaoIp expedicaoIp;
    private final EstoqueIp estoqueIp;
    private final ProcessoIp processoIp;
    private final MontagemIp montagemIp;
    private final SeletorTampaIp seletorTampaIp;

    private final ExpedicaoComm expedicaoComm;
    private final MontagemComm montagemComm;
    private final ProcessoComm processoComm;
    private final EstoqueComm estoqueComm;

    private final MonitoramentoService monitoramentoService;

    @Operation(summary = "Iniciar comunicação com os CLPs", description = "Recebe os IPs das 4 estações e do seletor de tampa, inicia os pollers e libera os endpoints de escrita.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comunicação iniciada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno ao conectar a algum CLP")
    })
    @PostMapping("/start")
    public ResponseEntity<Void> startComm(@RequestBody CommDto commDto) {

        estoqueIp.setIp(commDto.estoqueIp());
        processoIp.setIp(commDto.processoIp());
        montagemIp.setIp(commDto.montagemIp());
        expedicaoIp.setIp(commDto.expedicaoIp());
        seletorTampaIp.setEndpointApi(commDto.endpointSeletorTampa());
        
        estoqueComm.disconnect();
        processoComm.disconnect();
        montagemComm.disconnect();
        expedicaoComm.disconnect();

        estoqueComm.startComm();
        processoComm.startComm();
        montagemComm.startComm();
        expedicaoComm.startComm();

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Stream SSE de monitoramento geral", description = "Abre um stream Server-Sent Events com snapshots a cada 500 ms contendo status das 4 estações, posições do estoque e da expedição. Não testável diretamente pelo Swagger UI.")
    @ApiResponse(responseCode = "200", description = "Stream SSE aberto (text/event-stream)")
    @GetMapping(value = "/readAll", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter readAll() {
        log.info("SSE aberto");
        return monitoramentoService.conectar();
    }

    @Operation(summary = "Encerrar comunicação com os CLPs", description = "Desconecta os pollers e fecha as comunicações existentes.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comunicação encerrada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno ao deconectar a algum CLP")
    })
    @PostMapping("/close")
    public ResponseEntity<Void> closeComm() {
        log.info("Desconectado");
        estoqueComm.disconnect();
        processoComm.disconnect();
        montagemComm.disconnect();
        expedicaoComm.disconnect();

        return ResponseEntity.noContent().build();
    }

}
