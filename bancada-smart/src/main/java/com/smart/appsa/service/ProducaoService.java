package com.smart.appsa.service;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.appsa.dto.producao.ProducaoSnapshot;
import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.event.EstoqueAtualizadoEvent;
import com.smart.appsa.event.ExpedicaoLiberadaEvent;
import com.smart.appsa.event.ExpedicaoReservadaEvent;
import com.smart.appsa.mapper.cache.ProducaoCacheMapper;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ProducaoService {

    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private final BlocoService blocoService;
    private final ApplicationEventPublisher eventPublisher;


    public Pedido iniciarProducao(PedidoRequestDTO pedidoRequest) {
        Pedido pedido = pedidoRepository.findByCodPedido(pedidoRequest.codPedido())
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe"));

        pedido.getBlocos().forEach(b -> blocoService.assignEstoquePosition(b));

        pedido.setDataInicio(LocalDateTime.now());

        pedidoService.assignPosPedidoInExpedicao(pedido);

        pedido.getBlocos().forEach(b ->
                eventPublisher.publishEvent(new EstoqueAtualizadoEvent(this, b.getPosEstoque(), 0)));

        eventPublisher.publishEvent(
                new ExpedicaoReservadaEvent(this, pedido.getPosExpedicao(), pedido.getCodPedido()));

        //int codPedido = pedido.getCodPedido();

        //ProducaoSnapshot snapshot = ProducaoCacheMapper.constructSnapshot(pedido);

        //cacheRepository.salvar(codPedido, snapshot);

        try {
            pedido.setStatus(StatusPedido.PRODUCAO);
            pedidoRepository.save(pedido);
        } catch (RuntimeException e) {
            //cacheRepository.remover(codPedido);
            throw e;
        }

        return pedido;
    }

    @Transactional
    public void concluirProducao(int codPedido) {
        Pedido pedido = pedidoRepository.findByCodPedido(codPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe: " + codPedido));

        if (pedido.getStatus() == StatusPedido.CONCLUIDO) {
            //cacheRepository.remover(codPedido);
            return;
        }

        /*if (!cacheRepository.existe(codPedido)) {
            System.out.println("AVISO: conclusão do pedido " + codPedido
                    + " sem snapshot no Redis (possível inconsistência). Prosseguindo.");
        }*/

        pedidoService.updateToConcluido(pedido.getId());

        //cacheRepository.remover(codPedido);
    }

    @Transactional
    public void cancelarOuFalharProducao(int codPedido, String motivo) {
        Pedido pedido = pedidoRepository.findByCodPedido(codPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe: " + codPedido));

        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.CONCLUIDO) {
            //cacheRepository.remover(codPedido);
            return;
        }

        if (pedido.getPosExpedicao() != null) {
            eventPublisher.publishEvent(new ExpedicaoLiberadaEvent(this, pedido.getPosExpedicao()));
        }

        System.out.println("Cancelando/falhando produção do pedido " + codPedido + ". Motivo: " + motivo);
        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);

        //cacheRepository.remover(codPedido);
    }

    
}
