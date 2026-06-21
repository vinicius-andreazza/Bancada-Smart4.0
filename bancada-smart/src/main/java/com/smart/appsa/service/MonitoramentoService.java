package com.smart.appsa.service;

import com.smart.appsa.dto.PedidoResponseDTO;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.plc.EstacaoPlc;
import com.smart.appsa.model.plc.EstoquePlc;
import com.smart.appsa.model.plc.ExpedicaoPlc;
import com.smart.appsa.model.plc.MontagemPlc;
import com.smart.appsa.model.plc.ProcessoPlc;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.dto.ExpedicaoDTO;
import com.smart.appsa.dto.sse.EstacaoStatus;
import com.smart.appsa.dto.sse.SseDto;
import com.smart.appsa.mapper.EstoqueMapper;
import com.smart.appsa.mapper.ExpedicaoMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitoramentoService {
    
    private final PedidoService pedidoService;
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

    @Scheduled(fixedDelay = 500)
    public void enviarSnapshot() {
        if(emitters.size() == 0){
            return;
        }

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
        return SseDto.builder()
                .codPedidoAtual(estoquePlc.getNumeroPedido())
                .estacaoStatus(List.of(
                        status("ESTOQUE", estoquePlc),
                        status("PROCESSO", processoPlc),
                        status("MONTAGEM", montagemPlc),
                        status("EXPEDICAO", expedicaoPlc)))
                .duracao(calculateDuracao())
                .estoque(getDataEstoque())
                .expedicao(getDataExpedicao())
                .build();
    }

    private EstacaoStatus status(String nome, EstacaoPlc plc){
        return EstacaoStatus.builder()
                .estacao(nome)
                .status(plc.getStatus())
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

    private String calculateDuracao(){
        PedidoResponseDTO pedido = pedidoService.findByCodigo(estoquePlc.getNumeroPedido());
        if(pedido.status().equals(StatusPedido.CONCLUIDO)){
            resetStatusEstacao();
            return Duration.between(pedido.dataInicio(), pedido.dataEntrada()).toString();
        }
        return Duration.between(pedido.dataInicio(), LocalDateTime.now()).toString();
    }

    private List<EstoqueDTO> getDataEstoque(){
        byte positions[] = estoquePlc.getPosicoes();
        List<Estoque> listaEstoque = new ArrayList<>();
        for(int i=0;i<positions.length;i++){
            listaEstoque.add(Estoque.builder().posicao(i+1).cor(Integer.parseInt(positions[i]+"")).build());
        }

        return listaEstoque.stream().map(EstoqueMapper::toDto).toList();
    }

    private List<ExpedicaoDTO> getDataExpedicao(){
        int positions[] = expedicaoPlc.getPedidosExpedicao();
        List<Expedicao> listaExpedicao = new ArrayList<>();
        for(int i=0;i<positions.length;i++){
            listaExpedicao.add(Expedicao.builder().posicao(i+1).pedido(createPedido(positions[i])).build());
        }
        return listaExpedicao.stream().map(ExpedicaoMapper::toDto).toList();
    }

    private Pedido createPedido(int codPedido){
        return Pedido.builder().codPedido(codPedido).build();
    }

    private void resetStatusEstacao(){
        estoquePlc.setConcluidoOP(false);
        montagemPlc.setConcluidoOP(false);
        processoPlc.setConcluidoOP(false);
        expedicaoPlc.setConcluidoOP(false);
    }
}
