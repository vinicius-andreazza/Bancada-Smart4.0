package com.smart.appsa.service;

import com.smart.appsa.model.clp.EstoquePlc;
import com.smart.appsa.model.clp.ExpedicaoPlc;
import com.smart.appsa.model.clp.MontagemPlc;
import com.smart.appsa.model.clp.ProcessoPlc;
import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.appsa.dto.producao.ProducaoSnapshot;
import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.event.EstoqueAtualizadoEvent;
import com.smart.appsa.event.ExpedicaoLiberadaEvent;
import com.smart.appsa.event.ExpedicaoReservadaEvent;
import com.smart.appsa.event.IniciarPedidoEvent;
import com.smart.appsa.mapper.PedidoMapper;
import com.smart.appsa.mapper.cache.ProducaoCacheMapper;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;
import com.smart.appsa.repository.PedidoRepository;
import com.smart.appsa.repository.cache.CacheRepository;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ProducaoService {

    private final ExpedicaoPlc expedicaoPlc;
    private final MontagemPlc montagemPlc;
    private final ProcessoPlc processoPlc;
    private final EstoquePlc estoquePlc;
    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private final BlocoService blocoService;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheRepository cacheRepository;


    public List<PedidoResponseDTO> findPedidosInProducao(){
        return cacheRepository.buscarTodos().stream().map(p -> PedidoResponseDTO.builder().codPedido(p.codPedido()).tipoPedido(TipoPedido.fromValue(p.tipoPedido())).build()).toList();
    }

    public void adicionarPedido(PedidoRequestDTO pedidoRequest){
        Pedido pedido = pedidoRepository.findByCodPedido(pedidoRequest.codPedido())
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe"));

        int codPedido = pedido.getCodPedido();

        ProducaoSnapshot snapshot = ProducaoCacheMapper.constructSnapshot(pedido);
        System.out.println(snapshot);
        cacheRepository.salvar(codPedido, snapshot);

        try {
            pedidoRepository.save(pedido);
        } catch (RuntimeException e) {
            cacheRepository.remover(codPedido);
            throw e;
        }
        eventPublisher.publishEvent(new IniciarPedidoEvent(this, pedido));
    }

    public Pedido iniciarProducao() {
        if (!estacaoLivres()) return null;

        Pedido pedido = null;
        ProducaoSnapshot snapshot;
        while ((snapshot = cacheRepository.buscarMaisAntigo()) != null) {
            pedido = PedidoMapper.toEntity(pedidoService.findByCodigo(snapshot.codPedido()));
            cacheRepository.remover(snapshot.codPedido());
        }

        if (pedido == null) return null;

        pedido.getBlocos().forEach(b -> blocoService.assignEstoquePosition(b));

        pedido.setDataInicio(LocalDateTime.now());

        pedidoService.assignPosPedidoInExpedicao(pedido);

        pedido.getBlocos().forEach(b ->
                eventPublisher.publishEvent(new EstoqueAtualizadoEvent(this, b.getPosEstoque(), 0)));

        eventPublisher.publishEvent(
                new ExpedicaoReservadaEvent(this, pedido.getPosExpedicao(), pedido.getCodPedido()));


        pedido.setStatus(StatusPedido.PRODUCAO);
        pedidoRepository.save(pedido);

        return pedido;
    }

    @Transactional
    public void concluirProducao(int codPedido) {
        Pedido pedido = pedidoRepository.findByCodPedido(codPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe: " + codPedido));

        if (pedido.getStatus() == StatusPedido.CONCLUIDO) {
            cacheRepository.remover(codPedido);
            return;
        }

        pedidoService.updateToConcluido(pedido.getId());

        cacheRepository.remover(codPedido);
        resetStatusEstacao();
        eventPublisher.publishEvent(new IniciarPedidoEvent(this, null));
    }

    @Transactional
    public void cancelarOuFalharProducao(int codPedido, String motivo) {
        Pedido pedido = pedidoRepository.findByCodPedido(codPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe: " + codPedido));

        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.CONCLUIDO) {
            cacheRepository.remover(codPedido);
            return;
        }

        if (pedido.getPosExpedicao() != null) {
            eventPublisher.publishEvent(new ExpedicaoLiberadaEvent(this, pedido.getPosExpedicao()));
        }

        System.out.println("Cancelando/falhando produção do pedido " + codPedido + ". Motivo: " + motivo);
        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);

        cacheRepository.remover(codPedido);

        eventPublisher.publishEvent(new IniciarPedidoEvent(this, null));
    }

    private boolean estacaoLivres(){
        return (!estoquePlc.isConcluidoOP() && !estoquePlc.isOcupado())
         && (!processoPlc.isConcluidoOP())
          &&(!montagemPlc.isConcluidoOP() && !montagemPlc.isOcupado())
           && (!expedicaoPlc.isConcluidoOP());
    }

    private void resetStatusEstacao(){
        estoquePlc.setConcluidoOP(false);
        montagemPlc.setConcluidoOP(false);
        processoPlc.setConcluidoOP(false);
        expedicaoPlc.setConcluidoOP(false);
    }

    
}
