package com.smart.appsa.service.producao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smart.appsa.dto.producao.ProducaoSnapshot;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.repository.PedidoRepository;
import com.smart.appsa.service.ProducaoService;

@ExtendWith(MockitoExtension.class)
class ProducaoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProducaoCacheRepository cacheRepository;

    @InjectMocks
    private ProducaoService producaoService;

    private static final int COD = 42;

    private Pedido pedido(StatusPedido status) {
        return Pedido.builder().id(1L).codPedido(COD).status(status).build();
    }

    private ProducaoSnapshot snapshot() {
        return ProducaoSnapshot.builder().codPedido(COD).build();
    }

    @Test
    void iniciarProducaoGravaNoRedisAntesDeMudarStatus() {
        Pedido pedido = pedido(StatusPedido.PENDENTE);

        producaoService.iniciarProducao(pedido, snapshot());

        InOrder inOrder = inOrder(cacheRepository, pedidoRepository);
        inOrder.verify(cacheRepository).salvar(eq(COD), any());
        inOrder.verify(pedidoRepository).save(pedido);
        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.PRODUCAO);
    }

    @Test
    void falhaAoGravarNoRedisNaoAlteraStatus() {
        Pedido pedido = pedido(StatusPedido.PENDENTE);
        doThrow(new IllegalStateException("redis down")).when(cacheRepository).salvar(eq(COD), any());

        assertThatThrownBy(() -> producaoService.iniciarProducao(pedido, snapshot()))
                .isInstanceOf(IllegalStateException.class);

        verify(pedidoRepository, never()).save(any());
        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.PENDENTE);
    }

    @Test
    void falhaAoSalvarStatusRemoveChaveDoRedis() {
        Pedido pedido = pedido(StatusPedido.PENDENTE);
        when(pedidoRepository.save(any())).thenThrow(new RuntimeException("db down"));

        assertThatThrownBy(() -> producaoService.iniciarProducao(pedido, snapshot()))
                .isInstanceOf(RuntimeException.class);

        verify(cacheRepository).remover(COD);
    }

    @Test
    void concluirProducaoPersisteERemoveChave() {
        when(pedidoRepository.findByCodPedido(COD)).thenReturn(Optional.of(pedido(StatusPedido.PRODUCAO)));
        when(cacheRepository.existe(COD)).thenReturn(true);

        producaoService.concluirProducao(COD);

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(StatusPedido.CONCLUIDO);
        assertThat(captor.getValue().getDataEntrada()).isNotNull();
        verify(cacheRepository).remover(COD);
    }

    @Test
    void concluirProducaoIdempotenteNaoReprocessaPedidoJaConcluido() {
        when(pedidoRepository.findByCodPedido(COD)).thenReturn(Optional.of(pedido(StatusPedido.CONCLUIDO)));

        producaoService.concluirProducao(COD);

        verify(pedidoRepository, never()).save(any());
        verify(cacheRepository).remover(COD);
    }

    @Test
    void cancelarMarcaCanceladoERemoveChave() {
        when(pedidoRepository.findByCodPedido(COD)).thenReturn(Optional.of(pedido(StatusPedido.PRODUCAO)));

        producaoService.cancelarOuFalharProducao(COD, "teste");

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(StatusPedido.CANCELADO);
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
