package com.smart.appsa.service;

import com.smart.appsa.model.plc.EstacaoPlc;
import com.smart.appsa.model.plc.EstoquePlc;
import com.smart.appsa.model.plc.ExpedicaoPlc;
import com.smart.appsa.model.plc.MontagemPlc;
import com.smart.appsa.model.plc.ProcessoPlc;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.smart.appsa.dto.sse.EstacaoStatus;
import com.smart.appsa.dto.sse.SseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitoramentoService {
    private final ExpedicaoPlc expedicaoPlc;
    private final MontagemPlc montagemPlc;
    private final ProcessoPlc processoPlc;
    private final EstoquePlc estoquePlc;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter conectar() {
        SseEmitter emitter = new SseEmitter(0L);

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    public void enviarSnapshot() {
        List<SseEmitter> removidos = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(
                    SseEmitter.event()
                            .name("monitoramento")
                            .data(constructData())
                );
            } catch (Exception e) {
                removidos.add(emitter);
            }
        }
        System.out.println(constructData());
        emitters.removeAll(removidos);
    }

    private SseDto constructData(){
        return SseDto.builder()
                .codPedidoAtual(estoquePlc.getNumeroPedido())
                .estacaoStatus(List.of(
                        status("ESTOQUE", estoquePlc),
                        status("PROCESSO", processoPlc),
                        status("MONTAGEM", montagemPlc),
                        status("EXPEDICAO", expedicaoPlc)))
                .inicioPedido(null)
                .build();
    }

    private EstacaoStatus status(String nome, EstacaoPlc plc){
        return EstacaoStatus.builder()
                .estacao(nome)
                .status(plc.getStatus())
                .atualizadoEm(LocalDateTime.now())
                .build();
    }
}
