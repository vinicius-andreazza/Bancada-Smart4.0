package com.smart.appsa.service;

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
import com.smart.appsa.model.enums.StatusEstacao;

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

        emitters.removeAll(removidos);
    }

    private SseDto constructData(){
        EstacaoStatus estoque = EstacaoStatus.builder().estacao("ESTOQUE").status(getStatusEstacaoEstoque()).atualizadoEm(LocalDateTime.now()).build();
        EstacaoStatus processo = EstacaoStatus.builder().estacao("PROCESSO").status(getStatusEstacaoProcesso()).atualizadoEm(LocalDateTime.now()).build();
        EstacaoStatus montagem = EstacaoStatus.builder().estacao("MONTAGEM").status(getStatusEstacaoMontagem()).atualizadoEm(LocalDateTime.now()).build();
        EstacaoStatus expedicao = EstacaoStatus.builder().estacao("EXPEDICAO").status(getStatusEstacaoExpedicao()).atualizadoEm(LocalDateTime.now()).build();

        return SseDto.builder().codPedidoAtual(estoquePlc.getNumeroPedido()).estacaoStatus(List.of(estoque, processo, montagem, expedicao)).inicioPedido(null).build();
    }

    private StatusEstacao getStatusEstacaoEstoque(){
        if(estoquePlc.isCancelOp()){
            return StatusEstacao.CANCELADO;
        }
        if(estoquePlc.isFinishOp()){
            return StatusEstacao.FINALIZADO;
        }
        if(estoquePlc.isOcupado()){
            return StatusEstacao.OCUPADO;
        }
        return StatusEstacao.START;
    }

    private StatusEstacao getStatusEstacaoProcesso(){
        if(processoPlc.isCancelOP()){
            return StatusEstacao.CANCELADO;
        }
        if(processoPlc.isFinishOP()){
            return StatusEstacao.FINALIZADO;
        }
        if(processoPlc.isOcupado()){
            return StatusEstacao.OCUPADO;
        }
        return StatusEstacao.START;
    }

    private StatusEstacao getStatusEstacaoMontagem(){
        if(montagemPlc.isCancelOP()){
            return StatusEstacao.CANCELADO;
        }
        if(montagemPlc.isFinishOP()){
            return StatusEstacao.FINALIZADO;
        }
        if(montagemPlc.isOcupado()){
            return StatusEstacao.OCUPADO;
        }
        return StatusEstacao.START;
    }

    private StatusEstacao getStatusEstacaoExpedicao(){
        if(expedicaoPlc.isCancelOP()){
            return StatusEstacao.CANCELADO;
        }
        if(expedicaoPlc.isFinishOP()){
            return StatusEstacao.FINALIZADO;
        }
        if(expedicaoPlc.isOcupado()){
            return StatusEstacao.OCUPADO;
        }
        return StatusEstacao.START;
    }
}
