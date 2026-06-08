package com.smart.appsa;

import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.repository.EstoqueRepository;
import com.smart.appsa.service.EstoqueService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @InjectMocks
    private EstoqueService estoqueService;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Estoque estoqueComPosicao(int posicao, int cor) {
        Estoque e = new Estoque();
        e.setPosicao(posicao);
        e.setCor(cor);
        return e;
    }

    // =========================================================================
    // findAll
    // =========================================================================

    @Test
    @DisplayName("TC-E01 | findAll retorna todos os registros de estoque")
    void findAll_retornaTodos() {
        when(estoqueRepository.findAll())
                .thenReturn(List.of(estoqueComPosicao(1, 1), estoqueComPosicao(2, 2)));

        List<EstoqueDTO> resultado = estoqueService.findAll();

        assertThat(resultado).hasSize(2);
    }

    @Test
    @DisplayName("TC-E02 | findAll retorna lista vazia quando estoque vazio")
    void findAll_estoqueVazio_retornaListaVazia() {
        when(estoqueRepository.findAll()).thenReturn(List.of());

        assertThat(estoqueService.findAll()).isEmpty();
    }

    // =========================================================================
    // findByPosicao
    // =========================================================================

    @Test
    @DisplayName("TC-E03 | findByPosicao retorna estoque da posição correta")
    void findByPosicao_posicaoExistente_retornaEstoque() {
        when(estoqueRepository.findByPosicao(3))
                .thenReturn(Optional.of(estoqueComPosicao(3, 2)));

        EstoqueDTO dto = estoqueService.findByPosicao(3);

        assertThat(dto.posicao()).isEqualTo(3);
        assertThat(dto.cor()).isEqualTo(2);
    }

    @Test
    @DisplayName("TC-E04 | findByPosicao lança EntityNotFoundException para posição inexistente")
    void findByPosicao_posicaoInexistente_lancaException() {
        when(estoqueRepository.findByPosicao(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.findByPosicao(999))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }

    // =========================================================================
    // findByCor
    // =========================================================================

    @Test
    @DisplayName("TC-E05 | findByCor com cor válida retorna lista de posições")
    void findByCor_corValida_retornaLista() {
        when(estoqueRepository.findByCor(1))
                .thenReturn(List.of(estoqueComPosicao(1, 1), estoqueComPosicao(5, 1)));

        List<EstoqueDTO> resultado = estoqueService.findByCor(1);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(e -> e.cor() == 1);
    }

    @Test
    @DisplayName("TC-E06 | findByCor com cor 0 lança IllegalArgumentException (fora do range 1-3)")
    void findByCor_corZero_lancaException() {
        assertThatThrownBy(() -> estoqueService.findByCor(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cor invalida");
    }

    @Test
    @DisplayName("TC-E07 | findByCor com cor 4 lança IllegalArgumentException (fora do range 1-3)")
    void findByCor_corQuatro_lancaException() {
        assertThatThrownBy(() -> estoqueService.findByCor(4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cor invalida");
    }

    @Test
    @DisplayName("TC-E08 | findByCor com cor negativa lança IllegalArgumentException")
    void findByCor_corNegativa_lancaException() {
        assertThatThrownBy(() -> estoqueService.findByCor(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // =========================================================================
    // findFirstByCor
    // =========================================================================

    @Test
    @DisplayName("TC-E09 | findFirstByCor retorna primeira posição disponível da cor")
    void findFirstByCor_corExistente_retornaPrimeira() {
        when(estoqueRepository.findFirstByCor(2))
                .thenReturn(Optional.of(estoqueComPosicao(7, 2)));

        Estoque resultado = estoqueService.findFirstByCor(2);

        assertThat(resultado.getPosicao()).isEqualTo(7);
    }

    @Test
    @DisplayName("TC-E10 | findFirstByCor lança EntityNotFoundException quando cor não encontrada no estoque")
    void findFirstByCor_corSemEstoque_lancaException() {
        when(estoqueRepository.findFirstByCor(3)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.findFirstByCor(3))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("cor: 3");
    }

    // =========================================================================
    // put
    // =========================================================================

    @Test
    @DisplayName("TC-E11 | put atualiza cor da posição existente")
    void put_posicaoExistente_atualizaCor() {
        Estoque estoqueExistente = estoqueComPosicao(1, 1);
        when(estoqueRepository.findByPosicao(1)).thenReturn(Optional.of(estoqueExistente));
        when(estoqueRepository.save(any(Estoque.class))).thenAnswer(inv -> inv.getArgument(0));

        EstoqueDTO dtoAtualizado = new EstoqueDTO(1, 3);
        EstoqueDTO resultado = estoqueService.put(1, dtoAtualizado);

        assertThat(resultado.cor()).isEqualTo(3);
    }

    @Test
    @DisplayName("TC-E12 | put lança EntityNotFoundException para posição inexistente")
    void put_posicaoInexistente_lancaException() {
        when(estoqueRepository.findByPosicao(50)).thenReturn(Optional.empty());

        EstoqueDTO dto = new EstoqueDTO(50, 1);

        assertThatThrownBy(() -> estoqueService.put(50, dto))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
