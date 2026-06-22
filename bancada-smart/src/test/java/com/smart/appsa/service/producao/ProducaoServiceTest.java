package com.smart.appsa.service.producao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;
import com.smart.appsa.repository.PedidoRepository;
import com.smart.appsa.repository.ProducaoCacheRepository;
import com.smart.appsa.service.BlocoService;
import com.smart.appsa.service.PedidoService;
import com.smart.appsa.service.ProducaoService;

@ExtendWith(MockitoExtension.class)
class ProducaoServiceTest {

    @Mock
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProducaoCacheRepository cacheRepository;

    @Mock
    private BlocoService blocoService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ProducaoService producaoService;

    private static final int COD = 42;

    private Pedido pedido(StatusPedido status) {
        return Pedido.builder()
                .id(1L)
                .codPedido(COD)
                .status(status)
                .corTampa(CorTampa.AZUL)
                .tipoPedido(TipoPedido.SIMPLES)
                .blocos(List.of())
                .build();
    }

    private PedidoRequestDTO dto() {
        return PedidoRequestDTO.builder().codPedido(COD).build();
    }

    private void setupAssignExpedicao() {
        doAnswer(inv -> {
            ((Pedido) inv.getArgument(0)).setPosExpedicao(1);
            return null;
        }).when(pedidoService).assignPosPedidoInExpedicao(any(Pedido.class));
    }

    @Test
    void iniciarProducaoGravaNoRedisAntesDeMudarStatus() {
        Pedido pedido = pedido(StatusPedido.PENDENTE);
        when(pedidoRepository.findByCodPedido(COD)).thenReturn(Optional.of(pedido));
        setupAssignExpedicao();

        producaoService.iniciarProducao(dto());

        verify(cacheRepository).salvar(eq(COD), any());
        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.PRODUCAO);
    }

    @Test
    void falhaAoGravarNoRedisNaoAlteraStatus() {
        Pedido pedido = pedido(StatusPedido.PENDENTE);
        when(pedidoRepository.findByCodPedido(COD)).thenReturn(Optional.of(pedido));
        setupAssignExpedicao();
        doThrow(new IllegalStateException("redis down")).when(cacheRepository).salvar(eq(COD), any());

        assertThatThrownBy(() -> producaoService.iniciarProducao(dto()))
                .isInstanceOf(IllegalStateException.class);

        verify(pedidoRepository, never()).save(any());
        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.PENDENTE);
    }

    @Test
    void falhaAoSalvarStatusRemoveChaveDoRedis() {
        Pedido pedido = pedido(StatusPedido.PENDENTE);
        when(pedidoRepository.findByCodPedido(COD)).thenReturn(Optional.of(pedido));
        setupAssignExpedicao();
        when(pedidoRepository.save(any())).thenThrow(new RuntimeException("db down"));

        assertThatThrownBy(() -> producaoService.iniciarProducao(dto()))
                .isInstanceOf(RuntimeException.class);

        verify(cacheRepository).remover(COD);
    }

    @Test
    void concluirProducaoDelegaAtualizacaoERemoveChave() {
        when(pedidoRepository.findByCodPedido(COD)).thenReturn(Optional.of(pedido(StatusPedido.PRODUCAO)));
        when(cacheRepository.existe(COD)).thenReturn(true);

        producaoService.concluirProducao(COD);

        verify(pedidoService).updateToConcluido(1L);
        verify(cacheRepository).remover(COD);
    }

    @Test
    void concluirProducaoIdempotenteNaoReprocessaPedidoJaConcluido() {
        when(pedidoRepository.findByCodPedido(COD)).thenReturn(Optional.of(pedido(StatusPedido.CONCLUIDO)));

        producaoService.concluirProducao(COD);

        verify(pedidoService, never()).updateToConcluido(any());
        verify(cacheRepository).remover(COD);
    }

    @Test
    void cancelarMarcaCanceladoERemoveChave() {
        when(pedidoRepository.findByCodPedido(COD)).thenReturn(Optional.of(pedido(StatusPedido.PRODUCAO)));

        producaoService.cancelarOuFalharProducao(COD, "teste");

        verify(pedidoRepository).save(any(Pedido.class));
        verify(cacheRepository).remover(COD);
    }

    @Test
    void cancelarIdempotenteEmPedidoConcluido() {
        when(pedidoRepository.findByCodPedido(COD)).thenReturn(Optional.of(pedido(StatusPedido.CONCLUIDO)));

        producaoService.cancelarOuFalharProducao(COD, "teste");

        verify(pedidoRepository, never()).save(any());
        verify(cacheRepository).remover(COD);
    }
}
