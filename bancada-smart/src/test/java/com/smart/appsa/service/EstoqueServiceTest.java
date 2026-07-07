package com.smart.appsa.service;

import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.repository.EstoqueRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EstoqueService estoqueService;


    @Test
    void shouldReturnAllEstoquesWhenFindAll() {
        when(estoqueRepository.findAll())
                .thenReturn(List.of(estoqueComPosicao(1, 1), estoqueComPosicao(2, 2)));

        List<EstoqueDTO> resultado = estoqueService.findAll();

        assertThat(resultado).hasSize(2);
    }

    @Test
    void shouldReturnEmptyListWhenFindAllGivenNoEstoque() {
        when(estoqueRepository.findAll()).thenReturn(List.of());

        assertThat(estoqueService.findAll()).isEmpty();
    }

    @Test
    void shouldReturnEstoqueWhenFindByPosicaoGivenExistingPosicao() {
        when(estoqueRepository.findByPosicao(3))
                .thenReturn(Optional.of(estoqueComPosicao(3, 2)));

        EstoqueDTO dto = estoqueService.findByPosicao(3);

        assertThat(dto.posicao()).isEqualTo(3);
        assertThat(dto.cor()).isEqualTo(2);
    }

    @Test
    void shouldThrowEntityNotFoundWhenFindByPosicaoGivenUnknownPosicao() {
        when(estoqueRepository.findByPosicao(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.findByPosicao(999))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }


    @Test
    void shouldReturnPositionsWhenFindByCorGivenValidCor() {
        when(estoqueRepository.findByCor(1))
                .thenReturn(List.of(estoqueComPosicao(1, 1), estoqueComPosicao(5, 1)));

        List<EstoqueDTO> resultado = estoqueService.findByCor(1);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(e -> e.cor() == 1);
    }

    @Test
    void shouldThrowWhenFindByCorGivenCorAboveRange() {
        assertThatThrownBy(() -> estoqueService.findByCor(4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cor invalida");
    }

    @Test
    void shouldThrowWhenFindByCorGivenNegativeCor() {
        assertThatThrownBy(() -> estoqueService.findByCor(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldReturnFirstPositionWhenFindFirstByCorGivenExistingCor() {
        when(estoqueRepository.findFirstByCor(2))
                .thenReturn(Optional.of(estoqueComPosicao(7, 2)));

        Estoque resultado = estoqueService.findFirstByCor(2);

        assertThat(resultado.getPosicao()).isEqualTo(7);
    }

    @Test
    void shouldThrowEntityNotFoundWhenFindFirstByCorGivenNoEstoqueForCor() {
        when(estoqueRepository.findFirstByCor(3)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.findFirstByCor(3))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("cor: 3");
    }

    @Test
    void shouldUpdateCorWhenPutGivenExistingPosicao() {
        Estoque estoqueExistente = estoqueComPosicao(1, 1);
        when(estoqueRepository.findByPosicao(1)).thenReturn(Optional.of(estoqueExistente));
        when(estoqueRepository.save(any(Estoque.class))).thenAnswer(inv -> inv.getArgument(0));

        EstoqueDTO dtoAtualizado = new EstoqueDTO(null,1, 3);
        EstoqueDTO resultado = estoqueService.put(1, dtoAtualizado);

        assertThat(resultado.cor()).isEqualTo(3);
    }

    @Test
    void shouldThrowEntityNotFoundWhenPutGivenUnknownPosicao() {
        when(estoqueRepository.findByPosicao(50)).thenReturn(Optional.empty());

        EstoqueDTO dto = new EstoqueDTO(null, 50, 1);

        assertThatThrownBy(() -> estoqueService.put(50, dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldUpdateAllCoresWhenPutAllGivenValidList() {
        when(estoqueRepository.findByPosicao(1)).thenReturn(Optional.of(estoqueComPosicao(1, 1)));
        when(estoqueRepository.findByPosicao(2)).thenReturn(Optional.of(estoqueComPosicao(2, 1)));
        when(estoqueRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<EstoqueDTO> entrada = List.of(new EstoqueDTO(null,1, 3), new EstoqueDTO(null,2, 0));
        List<EstoqueDTO> resultado = estoqueService.putAll(entrada);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).anySatisfy(e -> {
            assertThat(e.posicao()).isEqualTo(1);
            assertThat(e.cor()).isEqualTo(3);
        });
        assertThat(resultado).anySatisfy(e -> {
            assertThat(e.posicao()).isEqualTo(2);
            assertThat(e.cor()).isEqualTo(0);
        });
    }

    @Test
    void shouldThrowEntityNotFoundWhenPutAllGivenUnknownPosicao() {
        when(estoqueRepository.findByPosicao(99)).thenReturn(Optional.empty());

        List<EstoqueDTO> entrada = List.of(new EstoqueDTO(null,99, 1));

        assertThatThrownBy(() -> estoqueService.putAll(entrada))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldSetCorToZeroWhenReleasePosition() {
        Estoque estoque = estoqueComPosicao(5, 2);
        when(estoqueRepository.findByPosicao(5)).thenReturn(Optional.of(estoque));

        estoqueService.releasePosition(5);

        assertThat(estoque.getCor()).isZero();
    }

    @Test
    void shouldThrowEntityNotFoundWhenReleasePositionGivenUnknownPosicao() {
        when(estoqueRepository.findByPosicao(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.releasePosition(99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void shouldSetCorWhenAddPosition() {
        Estoque estoque = estoqueComPosicao(3, 0);
        when(estoqueRepository.findByPosicao(3)).thenReturn(Optional.of(estoque));

        estoqueService.addPosition(3, 2);

        assertThat(estoque.getCor()).isEqualTo(2);
    }

    @Test
    void shouldThrowEntityNotFoundWhenAddPositionGivenUnknownPosicao() {
        when(estoqueRepository.findByPosicao(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.addPosition(99, 1))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldPublishEventWhenPut() {
        Estoque estoqueExistente = estoqueComPosicao(1, 1);
        when(estoqueRepository.findByPosicao(1)).thenReturn(Optional.of(estoqueExistente));
        when(estoqueRepository.save(any(Estoque.class))).thenAnswer(inv -> inv.getArgument(0));

        estoqueService.put(1, new EstoqueDTO(null, 1, 3));

        verify(eventPublisher).publishEvent(any());
    }

    private Estoque estoqueComPosicao(int posicao, int cor) {
        Estoque e = new Estoque();
        e.setPosicao(posicao);
        e.setCor(cor);
        return e;
    }
}
