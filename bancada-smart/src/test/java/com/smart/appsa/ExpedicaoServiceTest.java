package com.smart.appsa;

import com.smart.appsa.dto.ExpedicaoDTO;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.repository.ExpedicaoRepository;
import com.smart.appsa.repository.PedidoRepository;
import com.smart.appsa.service.ExpedicaoService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpedicaoServiceTest {

    @Mock private ExpedicaoRepository expedicaoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @InjectMocks private ExpedicaoService expedicaoService;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

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

    // =========================================================================
    // findAll
    // =========================================================================

    @Test
    @DisplayName("TC-X01 | findAll retorna todas as posições de expedição")
    void findAll_retornaTodasPosicoes() {
        when(expedicaoRepository.findAll())
                .thenReturn(List.of(expedicaoLivre(1L, 1), expedicaoLivre(2L, 2)));

        List<ExpedicaoDTO> resultado = expedicaoService.findAll();

        assertThat(resultado).hasSize(2);
    }

    // =========================================================================
    // findByPosicao
    // =========================================================================

    @Test
    @DisplayName("TC-X02 | findByPosicao lança RuntimeException para posição inexistente")
    void findByPosicao_inexistente_lancaException() {
        when(expedicaoRepository.findByPosicao(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expedicaoService.findByPosicao(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("não encontrada");
    }

    @Test
    @DisplayName("TC-X03 | findByPosicao retorna ExpedicaoDTO para posição existente")
    void findByPosicao_existente_retornaDTO() {
        when(expedicaoRepository.findByPosicao(1))
                .thenReturn(Optional.of(expedicaoLivre(1L, 1)));

        ExpedicaoDTO dto = expedicaoService.findByPosicao(1);

        assertThat(dto.posicao()).isEqualTo(1);
    }

    // =========================================================================
    // assignPedido
    // =========================================================================

    @Test
    @DisplayName("TC-X04 | assignPedido lança EntityNotFoundException quando expedição está cheia")
    void assignPedido_expedicaoCheia_lancaException() {
        when(expedicaoRepository.findFirstByPedidoIsNull()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expedicaoService.assignPedido(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Expedição cheio");
    }

    @Test
    @DisplayName("TC-X05 | assignPedido lança RuntimeException quando pedido não existe")
    void assignPedido_pedidoInexistente_lancaException() {
        when(expedicaoRepository.findFirstByPedidoIsNull())
                .thenReturn(Optional.of(expedicaoLivre(1L, 1)));
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expedicaoService.assignPedido(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Pedido");
    }

    @Test
    @DisplayName("TC-X06 | assignPedido atribui pedido à primeira posição livre")
    void assignPedido_posicaoLivre_atribuiPedido() {
        Expedicao posLivre = expedicaoLivre(1L, 3);
        Pedido pedido = new Pedido();
        pedido.setId(1L);

        when(expedicaoRepository.findFirstByPedidoIsNull()).thenReturn(Optional.of(posLivre));
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Expedicao resultado = expedicaoService.assignPedido(1L);

        assertThat(resultado.getPedido()).isEqualTo(pedido);
        assertThat(resultado.getPosicao()).isEqualTo(3);
    }

    // =========================================================================
    // releasePosicao
    // =========================================================================

    @Test
    @DisplayName("TC-X07 | releasePosicao lança RuntimeException para ID de expedição inexistente")
    void releasePosicao_expedicaoInexistente_lancaException() {
        when(expedicaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expedicaoService.releasePosicao(99L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("TC-X08 | releasePosicao lança IllegalStateException quando posição já está livre")
    void releasePosicao_posicaoJaLivre_lancaException() {
        when(expedicaoRepository.findById(1L))
                .thenReturn(Optional.of(expedicaoLivre(1L, 1)));

        assertThatThrownBy(() -> expedicaoService.releasePosicao(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("livre");
    }

    @Test
    @DisplayName("TC-X09 | releasePosicao libera posição ocupada com sucesso")
    void releasePosicao_posicaoOcupada_libera() {
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
}
