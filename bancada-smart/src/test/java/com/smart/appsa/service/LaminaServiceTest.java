package com.smart.appsa.service;

import com.smart.appsa.dto.LaminaDTO;
import com.smart.appsa.exception.DuplicatedLaminaPosition;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.model.enums.*;
import com.smart.appsa.repository.BlocoRepository;
import com.smart.appsa.repository.LaminaRepository;

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
class LaminaServiceTest {

    @Mock private LaminaRepository laminaRepository;
    @Mock private BlocoRepository blocoRepository;
    @InjectMocks private LaminaService laminaService;

    @Test
    void shouldThrowWhenCreateGivenNullCorLamina() {
        LaminaDTO dto = new LaminaDTO(null, null, PadraoLamina.CASA, PosicaoLamina.FRENTE);

        assertThatThrownBy(() -> laminaService.create(dto, blocoVazio()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cor");
    }

    @Test
    void shouldThrowWhenCreateGivenNullPosicaoLamina() {
        LaminaDTO dto = new LaminaDTO(null, CorLamina.AZUL, PadraoLamina.CASA, null);

        assertThatThrownBy(() -> laminaService.create(dto, blocoVazio()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("posição");
    }

    @Test
    void shouldThrowWhenCreateGivenDuplicatedPosicaoInBloco() {
        LaminaDTO dto = dtoValido();
        Lamina existente = laminaEntity(PosicaoLamina.ESQUERDA);

        when(laminaRepository.findByBloco(any())).thenReturn(List.of(existente));

        assertThatThrownBy(() -> laminaService.create(dto, blocoVazio()))
                .isInstanceOf(DuplicatedLaminaPosition.class)
                .hasMessageContaining("posição");
    }

    @Test
    void shouldSaveLaminaWhenCreateGivenValidData() {
        LaminaDTO dto = dtoValido();
        Lamina laminaSalva = laminaEntity(PosicaoLamina.ESQUERDA);
        laminaSalva.setId(1L);

        when(laminaRepository.findByBloco(any())).thenReturn(List.of());
        when(laminaRepository.save(any(Lamina.class))).thenReturn(laminaSalva);

        LaminaDTO resultado = laminaService.create(dto, blocoVazio());

        assertThat(resultado).isNotNull();
        verify(laminaRepository).save(any(Lamina.class));
    }


    @Test
    void shouldThrowEntityNotFoundWhenFindByBlocoGivenUnknownBloco() {
        when(blocoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> laminaService.findByBloco(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void shouldReturnLaminasWhenFindByBlocoGivenExistingBloco() {
        Bloco b = blocoVazio();
        Lamina l = laminaEntity(PosicaoLamina.FRENTE);
        l.setId(1L);
        when(blocoRepository.findById(1L)).thenReturn(Optional.of(b));
        when(laminaRepository.findByBloco(b)).thenReturn(List.of(l));

        List<Lamina> resultado = laminaService.findByBloco(1L);

        assertThat(resultado).hasSize(1);
    }


    @Test
    void shouldThrowEntityNotFoundWhenFindByIdGivenUnknownId() {
        when(laminaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> laminaService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }


    @Test
    void shouldThrowEntityNotFoundWhenDeleteGivenUnknownId() {
        when(laminaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> laminaService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldRemoveLaminaWhenDeleteGivenExistingId() {
        Lamina l = laminaEntity(PosicaoLamina.DIREITA);
        l.setId(1L);
        when(laminaRepository.findById(1L)).thenReturn(Optional.of(l));

        assertThatNoException().isThrownBy(() -> laminaService.delete(1L));
        verify(laminaRepository).delete(l);
    }

    @Test
    void shouldThrowEntityNotFoundWhenPutGivenUnknownId() {
        LaminaDTO dto = new LaminaDTO(99L, CorLamina.AZUL, PadraoLamina.CASA, PosicaoLamina.FRENTE);
        when(laminaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> laminaService.put(dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldUpdateAllFieldsWhenPutGivenExistingId() {
        Lamina existente = laminaEntity(PosicaoLamina.ESQUERDA);
        existente.setId(1L);
        when(laminaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(laminaRepository.save(any(Lamina.class))).thenAnswer(inv -> inv.getArgument(0));

        LaminaDTO dto = new LaminaDTO(1L, CorLamina.AZUL, PadraoLamina.NAVIO, PosicaoLamina.DIREITA);
        LaminaDTO resultado = laminaService.put(dto);

        assertThat(resultado.corLamina()).isEqualTo(CorLamina.AZUL);
        assertThat(resultado.posicaoLamina()).isEqualTo(PosicaoLamina.DIREITA);
        assertThat(resultado.padraoLamina()).isEqualTo(PadraoLamina.NAVIO);
    }

    @Test
    void shouldThrowEntityNotFoundWhenPatchGivenUnknownId() {
        LaminaDTO dto = new LaminaDTO(99L, CorLamina.AZUL, null, null);
        when(laminaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> laminaService.patch(dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldUpdateOnlyNonNullFieldsWhenPatch() {
        Lamina existente = laminaEntity(PosicaoLamina.ESQUERDA);
        existente.setId(1L);
        when(laminaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(laminaRepository.save(any(Lamina.class))).thenAnswer(inv -> inv.getArgument(0));

        LaminaDTO dto = new LaminaDTO(1L, CorLamina.VERDE, null, null);
        LaminaDTO resultado = laminaService.patch(dto);

        assertThat(resultado.corLamina()).isEqualTo(CorLamina.VERDE);
        assertThat(resultado.posicaoLamina()).isEqualTo(PosicaoLamina.ESQUERDA);
    }

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

}
