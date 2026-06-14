package com.smart.appsa.controller;

import com.smart.appsa.config.ipconfig.EstoqueIp;
import com.smart.appsa.config.ipconfig.ExpedicaoIp;
import com.smart.appsa.config.ipconfig.MontagemIp;
import com.smart.appsa.config.ipconfig.ProcessoIp;
import com.smart.appsa.config.ipconfig.SeletorTampaIp;
import com.smart.appsa.dto.CommDto;
import com.smart.appsa.service.MonitoramentoService;
import com.smart.appsa.service.clp.EstoqueComm;
import com.smart.appsa.service.clp.ExpedicaoComm;
import com.smart.appsa.service.clp.MontagemComm;
import com.smart.appsa.service.clp.ProcessoComm;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/smart")
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

    @PostMapping("/start")
    public ResponseEntity startComm(@RequestBody CommDto commDto) {

        estoqueIp.setIp(commDto.estoqueIp());
        processoIp.setIp(commDto.processoIp());
        montagemIp.setIp(commDto.montagemIp());
        expedicaoIp.setIp(commDto.expedicaoIp());
        seletorTampaIp.setEndpointApi(commDto.endpointSeletorTampa());

        estoqueComm.startComm();
        processoComm.startComm();
        montagemComm.startComm();
        expedicaoComm.startComm();

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/readAll", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter readAll() {
        return monitoramentoService.conectar();
    }
    
}
