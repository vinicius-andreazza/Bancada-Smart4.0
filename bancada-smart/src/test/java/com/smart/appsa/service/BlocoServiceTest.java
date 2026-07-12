package com.smart.appsa.service;

import com.smart.appsa.dto.BlocoDTO;
import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.dto.LaminaDTO;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.*;
import com.smart.appsa.repository.BlocoRepository;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlocoServiceTest {

    @Mock private BlocoRepository blocoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @Mock private LaminaService laminaService;
    @Mock private EstoqueService estoqueService;

    @InjectMocks private BlocoService blocoService;

    @Test
    void shouldThrowWhenCreateGivenNullCorBloco() {
        Pedido p = pedidoComId(1L);
        BlocoDTO dto = new BlocoDTO(null, null, null, AndarBloco.PRIMEIRO, p, List.of(laminaValida()));

        assertThatThrownBy(() -> blocoService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cor do bloco");
    }

    @Test
    void shouldThrowWhenCreateGivenNullPedido() {
        BlocoDTO dto = new BlocoDTO(null, CorBloco.PRETO, null, AndarBloco.PRIMEIRO, null, List.of(laminaValida()));

        assertThatThrownBy(() -> blocoService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pedido");
    }

    @Test
    void shouldAcceptBlocoWhenCreateGivenZeroLaminas() {
        Pedido p = pedidoComId(1L);
        BlocoDTO dto = new BlocoDTO(null, CorBloco.PRETO, null, AndarBloco.PRIMEIRO, p, List.of());

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));
        when(blocoRepository.save(any(Bloco.class))).thenAnswer(inv -> inv.getArgument(0));

        BlocoDTO resultado = blocoService.create(dto);

        assertThat(resultado).isNotNull();
        verifyNoInteractions(laminaService);
    }

    @Test
    void shouldThrowWhenCreateGivenMoreThanThreeLaminas() {
        Pedido p = pedidoComId(1L);
        BlocoDTO dto = dtoBlocoComLaminas(p, 4);

        assertThatThrownBy(() -> blocoService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    void shouldThrowEntityNotFoundWhenCreateGivenUnknownPedido() {
        Pedido p = pedidoComId(99L);
        BlocoDTO dto = new BlocoDTO(null, CorBloco.PRETO, null, AndarBloco.PRIMEIRO, p, List.of(laminaValida()));

        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blocoService.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Pedido");
    }

    @Test
    void shouldCreateLaminasWhenCreateGivenValidBlocoWithLaminas() {
        Pedido p = pedidoComId(1L);
        BlocoDTO dto = dtoBlocoComLaminas(p, 1);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));
        when(blocoRepository.save(any(Bloco.class))).thenAnswer(inv -> inv.getArgument(0));
        when(laminaService.create(any(), any())).thenReturn(laminaValida());

        BlocoDTO resultado = blocoService.create(dto);

        assertThat(resultado).isNotNull();
        verify(laminaService).create(any(), any());
    }

    @Test
    void shouldAssignEstoquePositionAndConsumeColorWhenAssignEstoquePosition() {
        Bloco bloco = new Bloco();
        bloco.setCorBloco(CorBloco.PRETO);
        Estoque posEstoque = estoqueComCor(7, CorBloco.PRETO.getValue());

        when(estoqueService.findFirstByCor(CorBloco.PRETO.getValue())).thenReturn(posEstoque);

        blocoService.assignEstoquePosition(bloco);

        assertThat(bloco.getPosEstoque()).isEqualTo(7);
        assertThat(posEstoque.getCor()).isZero();
        verify(estoqueService).put(eq(7), any(EstoqueDTO.class));
    }

    @Test
    void shouldThrowEntityNotFoundWhenFindByIdGivenUnknownId() {
        when(blocoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blocoService.findById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void shouldReturnBlocoWhenFindByIdGivenExistingId() {
        Bloco b = new Bloco();
        b.setId(1L);
        b.setCorBloco(CorBloco.AZUL);
        b.setAndar(AndarBloco.SEGUNDO);
        b.setLaminas(List.of());
        when(blocoRepository.findById(1L)).thenReturn(Optional.of(b));

        BlocoDTO dto = blocoService.findById(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.corBloco()).isEqualTo(CorBloco.AZUL);
    }

    @Test
    void shouldThrowEntityNotFoundWhenFindByPedidoGivenUnknownPedido() {
        when(pedidoRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blocoService.findByPedido(5L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldReturnBlocosWhenFindByPedidoGivenExistingPedido() {
        Pedido p = pedidoComId(1L);
        Bloco b = new Bloco();
        b.setId(1L);
        b.setPedido(p);
        b.setCorBloco(CorBloco.PRETO);
        b.setLaminas(List.of());
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));
        when(blocoRepository.findByPedido(p)).thenReturn(List.of(b));

        List<BlocoDTO> resultado = blocoService.findByPedido(1L);

        assertThat(resultado).hasSize(1);
    }

    @Test
    void shouldThrowEntityNotFoundWhenDeleteGivenUnknownId() {
        when(blocoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blocoService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldRemoveBlocoWhenDeleteGivenExistingId() {
        Bloco b = new Bloco();
        b.setId(1L);
        b.setLaminas(List.of());
        when(blocoRepository.findById(1L)).thenReturn(Optional.of(b));

        assertThatNoException().isThrownBy(() -> blocoService.delete(1L));
        verify(blocoRepository).delete(b);
    }

    private LaminaDTO laminaValida() {
        return new LaminaDTO(null, CorLamina.AZUL, PadraoLamina.CASA, PosicaoLamina.ESQUERDA);
    }

    private Pedido pedidoComId(Long id) {
        Pedido p = new Pedido();
        p.setId(id);
        return p;
    }

    private BlocoDTO dtoBlocoComLaminas(Pedido pedido, int qtdLaminas) {
        List<LaminaDTO> laminas = Collections.nCopies(qtdLaminas, laminaValida());
        return new BlocoDTO(null, CorBloco.PRETO, null, AndarBloco.PRIMEIRO, pedido, laminas);
    }

    private Estoque estoqueComCor(int posicao, int cor) {
        Estoque e = new Estoque();
        e.setPosicao(posicao);
        e.setCor(cor);
        return e;
    }
}
