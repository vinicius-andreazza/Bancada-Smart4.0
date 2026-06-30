package com.smart.appsa.service;

import com.smart.appsa.dto.ExpedicaoDTO;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.repository.ExpedicaoRepository;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpedicaoServiceTest {

    @Mock private ExpedicaoRepository expedicaoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @InjectMocks private ExpedicaoService expedicaoService;

    @Test
    void shouldReturnAllPositionsWhenFindAll() {
        when(expedicaoRepository.findAll())
                .thenReturn(List.of(expedicaoLivre(1L, 1), expedicaoLivre(2L, 2)));

        List<ExpedicaoDTO> resultado = expedicaoService.findAll();

        assertThat(resultado).hasSize(2);
    }

    @Test
    void shouldThrowWhenFindByPosicaoGivenUnknownPosicao() {
        when(expedicaoRepository.findByPosicao(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expedicaoService.findByPosicao(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("não encontrada");
    }

    @Test
    void shouldReturnDtoWhenFindByPosicaoGivenExistingPosicao() {
        when(expedicaoRepository.findByPosicao(1))
                .thenReturn(Optional.of(expedicaoLivre(1L, 1)));

        ExpedicaoDTO dto = expedicaoService.findByPosicao(1);

        assertThat(dto.posicao()).isEqualTo(1);
    }

    @Test
    void shouldThrowEntityNotFoundWhenAssignPedidoGivenExpedicaoFull() {
        when(expedicaoRepository.findFirstByPedidoIsNull()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expedicaoService.assignPedido(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Expedição cheio");
    }

    @Test
    void shouldThrowWhenAssignPedidoGivenUnknownPedido() {
        when(expedicaoRepository.findFirstByPedidoIsNull())
                .thenReturn(Optional.of(expedicaoLivre(1L, 1)));
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expedicaoService.assignPedido(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Pedido");
    }

    @Test
    void shouldAssignPedidoToFirstFreePositionWhenAssignPedido() {
        Expedicao posLivre = expedicaoLivre(1L, 3);
        Pedido pedido = new Pedido();
        pedido.setId(1L);

        when(expedicaoRepository.findFirstByPedidoIsNull()).thenReturn(Optional.of(posLivre));
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Expedicao resultado = expedicaoService.assignPedido(1L);

        assertThat(resultado.getPedido()).isEqualTo(pedido);
        assertThat(resultado.getPosicao()).isEqualTo(3);
    }

    @Test
    void shouldThrowWhenReleasePosicaoGivenUnknownExpedicao() {
        when(expedicaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expedicaoService.releasePosicao(99L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldThrowIllegalStateWhenReleasePosicaoGivenAlreadyFree() {
        when(expedicaoRepository.findById(1L))
                .thenReturn(Optional.of(expedicaoLivre(1L, 1)));

        assertThatThrownBy(() -> expedicaoService.releasePosicao(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("livre");
    }

    @Test
    void shouldReleaseOccupiedPositionWhenReleasePosicao() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setPosExpedicao(2);

        Expedicao expedicao = expedicaoOcupada(1L, 2, pedido);

        when(expedicaoRepository.findById(1L)).thenReturn(Optional.of(expedicao));
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(expedicaoRepository.save(any(Expedicao.class))).thenAnswer(inv -> inv.getArgument(0));

        Expedicao resultado = expedicaoService.releasePosicao(1L);

        assertThat(resultado.getPedido()).isNull();
        verify(expedicaoRepository).save(any(Expedicao.class));
    }

    private Expedicao expedicaoLivre(Long id, int posicao) {
        Expedicao e = new Expedicao();
        e.setId(id);
        e.setPosicao(posicao);
        e.setPedido(null);
        return e;
    }

    private Expedicao expedicaoOcupada(Long id, int posicao, Pedido pedido) {
        Expedicao e = new Expedicao();
        e.setId(id);
        e.setPosicao(posicao);
        e.setPedido(pedido);
        return e;
    }
}
