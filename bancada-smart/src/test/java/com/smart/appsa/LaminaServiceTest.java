package com.smart.appsa;

import com.smart.appsa.dto.LaminaDTO;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.model.enums.*;
import com.smart.appsa.repository.BlocoRepository;
import com.smart.appsa.repository.LaminaRepository;
import com.smart.appsa.service.LaminaService;

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
class LaminaServiceTest {

    @Mock private LaminaRepository laminaRepository;
    @Mock private BlocoRepository blocoRepository;
    @InjectMocks private LaminaService laminaService;

    // -------------------------------------------------------------------------
    // Helpers
    // LaminaDTO: (Long id, CorLamina corLamina, PadraoLamina padraoLamina, PosicaoLamina posicaoLamina)
    // -------------------------------------------------------------------------

    private Bloco blocoVazio() {
        Bloco b = new Bloco();
        b.setId(1L);
        return b;
    }

    private LaminaDTO dtoValido() {
        return new LaminaDTO(null, CorLamina.AZUL, PadraoLamina.CASA, PosicaoLamina.ESQUERDA);
    }

    private Lamina laminaEntity(PosicaoLamina posicao) {
        Lamina l = new Lamina();
        l.setCorLamina(CorLamina.VERMELHO);
        l.setPadraoLamina(PadraoLamina.NAVIO);
        l.setPosicaoLamina(posicao);
        return l;
    }

    // =========================================================================
    // create — validações
    // =========================================================================

    @Test
    @DisplayName("TC-L01 | create lança IllegalArgumentException quando corLamina é nula")
    void create_semCor_lancaException() {
        LaminaDTO dto = new LaminaDTO(null, null, PadraoLamina.CASA, PosicaoLamina.FRENTE);
        when(laminaRepository.findByBloco(any())).thenReturn(List.of());

        assertThatThrownBy(() -> laminaService.create(dto, blocoVazio()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cor");
    }

    @Test
    @DisplayName("TC-L02 | create lança IllegalArgumentException quando padraoLamina é nulo")
    void create_semPadrao_lancaException() {
        LaminaDTO dto = new LaminaDTO(null, CorLamina.AZUL, null, PosicaoLamina.FRENTE);
        when(laminaRepository.findByBloco(any())).thenReturn(List.of());

        assertThatThrownBy(() -> laminaService.create(dto, blocoVazio()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("padrão");
    }

    @Test
    @DisplayName("TC-L03 | create lança IllegalArgumentException quando posicaoLamina é nula")
    void create_semPosicao_lancaException() {
        LaminaDTO dto = new LaminaDTO(null, CorLamina.AZUL, PadraoLamina.CASA, null);
        when(laminaRepository.findByBloco(any())).thenReturn(List.of());

        assertThatThrownBy(() -> laminaService.create(dto, blocoVazio()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("posição");
    }

    @Test
    @DisplayName("TC-L04 | create lança IllegalArgumentException quando posição já ocupada no bloco")
    void create_posicaoDuplicada_lancaException() {
        LaminaDTO dto = dtoValido(); // posição ESQUERDA
        Lamina existente = laminaEntity(PosicaoLamina.ESQUERDA); // já existe ESQUERDA

        when(laminaRepository.findByBloco(any())).thenReturn(List.of(existente));

        assertThatThrownBy(() -> laminaService.create(dto, blocoVazio()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("posição");
    }

    @Test
    @DisplayName("TC-L05 | create salva lâmina com dados válidos e posição livre")
    void create_dadosValidos_salvaComSucesso() {
        LaminaDTO dto = dtoValido();
        Lamina laminaSalva = laminaEntity(PosicaoLamina.ESQUERDA);
        laminaSalva.setId(1L);

        when(laminaRepository.findByBloco(any())).thenReturn(List.of());
        when(laminaRepository.save(any(Lamina.class))).thenReturn(laminaSalva);

        LaminaDTO resultado = laminaService.create(dto, blocoVazio());

        assertThat(resultado).isNotNull();
        verify(laminaRepository).save(any(Lamina.class));
    }

    // =========================================================================
    // findByBloco
    // =========================================================================

    @Test
    @DisplayName("TC-L06 | findByBloco lança EntityNotFoundException quando bloco não existe")
    void findByBloco_blocoInexistente_lancaException() {
        when(blocoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> laminaService.findByBloco(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("TC-L07 | findByBloco retorna lista de lâminas do bloco")
    void findByBloco_blocoExistente_retornaLaminas() {
        Bloco b = blocoVazio();
        Lamina l = laminaEntity(PosicaoLamina.FRENTE);
        l.setId(1L);
        when(blocoRepository.findById(1L)).thenReturn(Optional.of(b));
        when(laminaRepository.findByBloco(b)).thenReturn(List.of(l));

        List<Lamina> resultado = laminaService.findByBloco(1L);

        assertThat(resultado).hasSize(1);
    }

    // =========================================================================
    // findById
    // =========================================================================

    @Test
    @DisplayName("TC-L08 | findById lança EntityNotFoundException para ID inexistente")
    void findById_idInexistente_lancaException() {
        when(laminaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> laminaService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // =========================================================================
    // delete
    // =========================================================================

    @Test
    @DisplayName("TC-L09 | delete lança EntityNotFoundException para ID inexistente")
    void delete_idInexistente_lancaException() {
        when(laminaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> laminaService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("TC-L10 | delete remove lâmina existente sem lançar exceção")
    void delete_idExistente_removeLamina() {
        Lamina l = laminaEntity(PosicaoLamina.DIREITA);
        l.setId(1L);
        when(laminaRepository.findById(1L)).thenReturn(Optional.of(l));

        assertThatNoException().isThrownBy(() -> laminaService.delete(1L));
        verify(laminaRepository).delete(l);
    }
}