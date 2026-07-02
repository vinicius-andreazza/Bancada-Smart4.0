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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expedicao")
@Slf4j
public class ExpedicaoController {
    private final ExpedicaoService expedicaoService;

    @GetMapping("")
    public ResponseEntity<List<ExpedicaoDTO>> findAll(){
        return ResponseEntity.ok(expedicaoService.findAll());
    }

    @GetMapping(value = "/read", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter readAll() {
        log.info("SSE aberto com Expedição");
        return expedicaoService.conectar();
    }

}
