package com.smart.appsa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.smart.appsa.dto.ExpedicaoDTO;
import com.smart.appsa.event.ExpedicaoLiberadaEvent;
import com.smart.appsa.event.ExpedicaoReservadaEvent;
import com.smart.appsa.event.IniciarPedidoEvent;
import com.smart.appsa.mapper.ExpedicaoMapper;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.repository.ExpedicaoRepository;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpedicaoService {

    private final ExpedicaoRepository expedicaoRepository;
    private final PedidoRepository pedidoRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter conectar() {
        SseEmitter emitter = new SseEmitter(0L);
        
        emitters.add(emitter);
        enviarSnapshot();
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        
        return emitter;
    }

    @EventListener
    @Order(2)
    public void posicaoReservadaEventListener(ExpedicaoReservadaEvent event) {
        enviarSnapshot();
    }

    @EventListener
    @Order(2)
    public void posicaoLiberadaEventListener(ExpedicaoLiberadaEvent event) {
        enviarSnapshot();
    }

    public void enviarSnapshot() {
        if(emitters.size() == 0){
            return;
        }

        List<SseEmitter> removidos = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(
                    SseEmitter.event()
                            .name("expedicao")
                            .data(findAll())
                );
            } catch (Exception e) {
                removidos.add(emitter);
            }
        }
        
        emitters.removeAll(removidos);
    }

    public Expedicao findById(Long id) {
        return expedicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expedição não encontrada com id: " + id));
    }

    public ExpedicaoDTO findByPosicao(Integer posicao) {
        return ExpedicaoMapper.toDto(expedicaoRepository.findByPosicao(posicao)
                .orElseThrow(() -> new RuntimeException("Expedição não encontrada")));
    }

    public List<ExpedicaoDTO> findAll() {
        return expedicaoRepository.findAll().stream().map(ExpedicaoMapper::toDto).toList();
    }

    public List<Expedicao> findAllExpedicao() {
        return expedicaoRepository.findAll();
    }

    public Expedicao findFirstAvailable (){
        return expedicaoRepository.findFirstByPedidoIsNull().orElseThrow(() -> new EntityNotFoundException("Expedição cheio"));
    }

    
    public Expedicao assignPedido(Long pedidoId) {
        Expedicao expedicao = expedicaoRepository.findFirstByPedidoIsNull().orElseThrow(() -> new EntityNotFoundException("Expedição cheio"));

        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new RuntimeException("Pedido não encontrado "));
        
        expedicao.setPedido(pedido);
        enviarSnapshot();
        return expedicao;
    }

    public Expedicao assignPedidoByPosition(Long pedidoId, int position) {
        Expedicao expedicao = expedicaoRepository.findByPosicao(position).orElseThrow(() -> new IllegalArgumentException("Posição não existe"));

        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new RuntimeException("Pedido não encontrado "));
        
        expedicao.setPedido(pedido);
        enviarSnapshot();
        return expedicao;
    }

    public Expedicao releasePosicao(Long expedicaoId) {
        Expedicao expedicao = expedicaoRepository.findById(expedicaoId)
                .orElseThrow(() -> new RuntimeException("Expedição não encontrada com id: " + expedicaoId));

        if (expedicao.getPedido() == null)
            throw new IllegalStateException("A posição " + expedicao.getPosicao() + " já está livre.");

        releasePedido(expedicao);

        expedicao.setPedido(null);
        eventPublisher.publishEvent(new ExpedicaoLiberadaEvent(this, expedicao.getPosicao()));
        enviarSnapshot();
        return expedicaoRepository.save(expedicao);
    }

    private void releasePedido(Expedicao expedicao){
        Pedido pedido = pedidoRepository.findById(expedicao.getPedido().getId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado "));
        pedido.setPosExpedicao(null);
        pedidoRepository.save(pedido);
    }

}
