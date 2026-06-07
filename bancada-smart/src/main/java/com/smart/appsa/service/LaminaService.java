package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smart.appsa.dto.LaminaDTO;
import com.smart.appsa.mapper.LaminaMapper;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.repository.BlocoRepository;
import com.smart.appsa.repository.LaminaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LaminaService {

    private final LaminaRepository laminaRepository;
    private final BlocoRepository blocoRepository;

    @Transactional
    public LaminaDTO create(LaminaDTO laminaDTO, Bloco bloco) {
        Lamina lamina = LaminaMapper.toEntity(laminaDTO);

        validateLamina(lamina);

        validatePositionLamina(lamina, bloco);

        lamina.setBloco(bloco);
        
        return LaminaMapper.toDto(laminaRepository.save(lamina));
    }

    public List<Lamina> findByBloco(Long blocoId) {
        Bloco bloco = blocoRepository.findById(blocoId)
                .orElseThrow(() -> new RuntimeException("Bloco não encontrado com id: " + blocoId));
        return laminaRepository.findByBloco(bloco);
    }

    public Lamina findById(Long id) {
        return laminaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lâmina não encontrada com id: " + id));
    }

    @Transactional
    public void delete(Long id) {
        Lamina lamina = findById(id);
        laminaRepository.delete(lamina);
    }

    private void validateLamina(Lamina lamina) {
        if (lamina.getCorLamina() == null) {
            throw new IllegalArgumentException("A cor da lâmina é obrigatória.");
        }
        if (lamina.getPadraoLamina() == null) {
            throw new IllegalArgumentException("O padrão da lâmina é obrigatório.");
        }
        if (lamina.getPosicaoLamina() == null) {
            throw new IllegalArgumentException("A posição da lâmina é obrigatória.");
        }
    }

    private void validatePositionLamina(Lamina lamina, Bloco bloco){
        boolean posicaoOcupada = laminaRepository.findByBloco(bloco).stream()
                .anyMatch(l -> l.getPosicaoLamina() == lamina.getPosicaoLamina());
        if (posicaoOcupada) {
            throw new IllegalArgumentException(
                    "Já existe uma lâmina na posição " + lamina.getPosicaoLamina() + " para este bloco.");
        }
    }
}