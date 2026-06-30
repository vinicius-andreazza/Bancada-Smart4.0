package com.smart.appsa.service;

import com.smart.appsa.model.clp.EstoquePlc;
import com.smart.appsa.model.clp.ExpedicaoPlc;
import com.smart.appsa.model.clp.MontagemPlc;
import com.smart.appsa.model.clp.ProcessoPlc;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.event.EstoqueAtualizadoEvent;
import com.smart.appsa.event.ExpedicaoLiberadaEvent;
import com.smart.appsa.event.ExpedicaoReservadaEvent;
import com.smart.appsa.event.IniciarPedidoEvent;
import com.smart.appsa.mapper.PedidoMapper;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProducaoService {

    private final ExpedicaoPlc expedicaoPlc;
    private final MontagemPlc montagemPlc;
    private final ProcessoPlc processoPlc;
    private final EstoquePlc estoquePlc;
    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private final BlocoService blocoService;
    private final ApplicationEventPublisher eventPublisher;
    private final FilaProducao filaProducao;


    public List<PedidoResponseDTO> findPedidosInProducao() {
        return filaProducao.listar().stream()
                .map(cod -> pedidoRepository.findByCodPedido(cod).orElse(null))
                .filter(Objects::nonNull)
                .map(PedidoMapper::toResponse)
                .toList();
    }

    public void adicionarPedido(PedidoRequestDTO pedidoRequest) {
        Pedido pedido = pedidoRepository.findByCodPedido(pedidoRequest.codPedido())
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe"));

        filaProducao.adicionar(pedido.getCodPedido());

        eventPublisher.publishEvent(new IniciarPedidoEvent(this, pedido));
    }

    public Pedido iniciarProducao() {
        if (!estacaoLivres()) return null;

        Integer codPedido = filaProducao.poll();
        if (codPedido == null) return null;

        Pedido pedido = PedidoMapper.toEntity(pedidoService.findByCodigo(codPedido));

        pedido.getBlocos().forEach(b -> blocoService.assignEstoquePosition(b));

        pedido.setDataInicio(LocalDateTime.now());

        pedidoService.assignPosPedidoInExpedicao(pedido);

        pedido.getBlocos().forEach(b ->
                eventPublisher.publishEvent(new EstoqueAtualizadoEvent(this, b.getPosEstoque(), 0)));

        pedido.setStatus(StatusPedido.PRODUCAO);
        pedidoRepository.save(pedido);

        return pedido;
    }

    @Transactional
    public void concluirProducao(int codPedido) {
        Pedido pedido = pedidoRepository.findByCodPedido(codPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe: " + codPedido));

        if (pedido.getStatus() == StatusPedido.CONCLUIDO) {
            return;
        }

        pedidoService.updateToConcluido(pedido.getId());
        eventPublisher.publishEvent(
            new ExpedicaoReservadaEvent(this, pedido.getPosExpedicao(), pedido.getCodPedido()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("Erro no sleep ao concluir pedido ", e);
        }

        resetStatusEstacao();

        eventPublisher.publishEvent(new IniciarPedidoEvent(this, null));
    }

    @Transactional
    public void cancelarOuFalharProducao(int codPedido, String motivo) {
        Pedido pedido = pedidoRepository.findByCodPedido(codPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe: " + codPedido));

        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.CONCLUIDO) {
            return;
        }

        if (pedido.getPosExpedicao() != null) {
            eventPublisher.publishEvent(new ExpedicaoLiberadaEvent(this, pedido.getPosExpedicao()));
        }

        System.out.println("Cancelando/falhando produção do pedido " + codPedido + ". Motivo: " + motivo);
        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("Erro no sleep ao cancelar pedido ", e);
        }
        eventPublisher.publishEvent(new IniciarPedidoEvent(this, null));
    }

    private boolean estacaoLivres() {
        return (!estoquePlc.isConcluidoOP() && !estoquePlc.isOcupado())
                && (!processoPlc.isConcluidoOP() && !processoPlc.isConcluidoOP())
                && (!montagemPlc.isConcluidoOP() && !montagemPlc.isOcupado())
                && (!expedicaoPlc.isConcluidoOP());
    }

    private void resetStatusEstacao() {
        estoquePlc.setConcluidoOP(false);
        montagemPlc.setConcluidoOP(false);
        processoPlc.setConcluidoOP(false);
        expedicaoPlc.setConcluidoOP(false);
    }
}
