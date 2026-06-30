package com.smart.appsa.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.appsa.dto.ExpedicaoDTO;
import com.smart.appsa.service.ExpedicaoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expedicao")
public class ExpedicaoController {
    private final ExpedicaoService expedicaoService;

    @GetMapping("")
    public ResponseEntity<List<ExpedicaoDTO>> findAll(){
        return ResponseEntity.ok(expedicaoService.findAll());
    }
}
