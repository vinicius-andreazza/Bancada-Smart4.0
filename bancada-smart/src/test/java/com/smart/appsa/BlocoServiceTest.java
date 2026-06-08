package com.smart.appsa;

import com.smart.appsa.dto.BlocoDTO;
import com.smart.appsa.dto.LaminaDTO;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.*;
import com.smart.appsa.repository.BlocoRepository;
import com.smart.appsa.repository.PedidoRepository;
import com.smart.appsa.service.BlocoService;
import com.smart.appsa.service.EstoqueService;
import com.smart.appsa.service.LaminaService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlocoServiceTest {

    @Mock private BlocoRepository blocoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @Mock private LaminaService laminaService;
    @Mock private EstoqueService estoqueService;

    @InjectMocks private BlocoService blocoService;

    // -------------------------------------------------------------------------
    // Helpers
    // BlocoDTO: (Long id, CorBloco corBloco, Integer posEstoque, AndarBloco andar, Pedido pedido, List<LaminaDTO> laminas)
    // LaminaDTO: (Long id, CorLamina corLamina, PadraoLamina padraoLamina, PosicaoLamina posicaoLamina)
    // -------------------------------------------------------------------------

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

    // =========================================================================
    // create — validações
    // =========================================================================

    @Test
    @DisplayName("TC-B01 | create lança IllegalArgumentException quando corBloco é nula")
    void create_semCorBloco_lancaException() {
        Pedido p = pedidoComId(1L);
        BlocoDTO dto = new BlocoDTO(null, null, null, AndarBloco.PRIMEIRO, p, List.of(laminaValida()));

        assertThatThrownBy(() -> blocoService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cor do bloco");
    }

    @Test
    @DisplayName("TC-B02 | create lança IllegalArgumentException quando pedido é nulo")
    void create_semPedido_lancaException() {
        BlocoDTO dto = new BlocoDTO(null, CorBloco.PRETO, null, AndarBloco.PRIMEIRO, null, List.of(laminaValida()));

        assertThatThrownBy(() -> blocoService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pedido");
    }

    @Test
    @DisplayName("TC-B03 | create lança IllegalArgumentException com lista de lâminas vazia")
    void create_semLaminas_lancaException() {
        Pedido p = pedidoComId(1L);
        BlocoDTO dto = new BlocoDTO(null, CorBloco.PRETO, null, AndarBloco.PRIMEIRO, p, List.of());

        assertThatThrownBy(() -> blocoService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    @DisplayName("TC-B04 | create lança IllegalArgumentException com mais de 3 lâminas")
    void create_quatroLaminas_lancaException() {
        Pedido p = pedidoComId(1L);
        BlocoDTO dto = dtoBlocoComLaminas(p, 4);

        assertThatThrownBy(() -> blocoService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    @DisplayName("TC-B05 | create lança EntityNotFoundException quando pedido referenciado não existe")
    void create_pedidoInexistente_lancaException() {
        Pedido p = pedidoComId(99L);
        BlocoDTO dto = new BlocoDTO(null, CorBloco.PRETO, null, AndarBloco.PRIMEIRO, p, List.of(laminaValida()));

        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blocoService.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Pedido");
    }

    // =========================================================================
    // findById
    // =========================================================================

    @Test
    @DisplayName("TC-B06 | findById lança EntityNotFoundException para ID inexistente")
    void findById_idInexistente_lancaException() {
        when(blocoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blocoService.findById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("TC-B07 | findById retorna bloco para ID existente")
    void findById_idExistente_retornaBloco() {
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

    // =========================================================================
    // findByPedido
    // =========================================================================

    @Test
    @DisplayName("TC-B08 | findByPedido lança EntityNotFoundException quando pedido não existe")
    void findByPedido_pedidoInexistente_lancaException() {
        when(pedidoRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blocoService.findByPedido(5L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("TC-B09 | findByPedido retorna blocos associados ao pedido")
    void findByPedido_pedidoExistente_retornaBlocos() {
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

    // =========================================================================
    // delete
    // =========================================================================

    @Test
    @DisplayName("TC-B10 | delete lança EntityNotFoundException para ID inexistente")
    void delete_idInexistente_lancaException() {
        when(blocoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blocoService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("TC-B11 | delete remove bloco existente sem lançar exceção")
    void delete_idExistente_removeBloco() {
        Bloco b = new Bloco();
        b.setId(1L);
        b.setLaminas(List.of());
        when(blocoRepository.findById(1L)).thenReturn(Optional.of(b));

        assertThatNoException().isThrownBy(() -> blocoService.delete(1L));
        verify(blocoRepository).delete(b);
    }
}