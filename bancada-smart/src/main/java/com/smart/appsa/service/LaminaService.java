package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.repository.BlocoRepository;
import com.smart.appsa.repository.LaminaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LaminaService {

    private final LaminaRepository laminaRepository;
    private final BlocoRepository blocoRepository;


    public Lamina buscarLaminaPorId(Long id) {
        return laminaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lâmina não encontrada com id: " + id));
    }

    public List<Lamina> listarLaminasPorBloco(Long blocoId) {
        Bloco bloco = buscarBloco(blocoId);
        return laminaRepository.findByBloco(bloco);
    }

    public Lamina criarLamina(Lamina lamina) {
        validarLamina(lamina);

        Bloco bloco = buscarBloco(lamina.getBloco().getId());

        boolean posicaoOcupada = laminaRepository.findByBloco(bloco).stream()
                .anyMatch(l -> l.getPosicaoLamina() == lamina.getPosicaoLamina());
        if (posicaoOcupada) {
            throw new IllegalArgumentException(
                    "Já existe uma lâmina na posição " + lamina.getPosicaoLamina() + " para este bloco.");
        }

        lamina.setBloco(bloco);
        return laminaRepository.save(lamina);
    }

    public Lamina atualizarLamina(Long id, Lamina lamina) {
        validarLamina(lamina);

        Lamina laminaExistente = buscarLaminaPorId(id);
        Bloco bloco = buscarBloco(lamina.getBloco().getId());

        boolean posicaoAlterada = laminaExistente.getPosicaoLamina() != lamina.getPosicaoLamina();
        if (posicaoAlterada) {
            boolean posicaoOcupada = laminaRepository.findByBloco(bloco).stream()
                    .anyMatch(l -> !l.getId().equals(id) && l.getPosicaoLamina() == lamina.getPosicaoLamina());
            if (posicaoOcupada) {
                throw new IllegalArgumentException(
                        "Já existe uma lâmina na posição " + lamina.getPosicaoLamina() + " para este bloco.");
            }
        }

        laminaExistente.setCorLamina(lamina.getCorLamina());
        laminaExistente.setPadraoLamina(lamina.getPadraoLamina());
        laminaExistente.setPosicaoLamina(lamina.getPosicaoLamina());
        laminaExistente.setBloco(bloco);

        return laminaRepository.save(laminaExistente);
    }

    public void deletarLamina(Long id) {
        Lamina lamina = buscarLaminaPorId(id);
        laminaRepository.delete(lamina);
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares
    // -------------------------------------------------------------------------
    private Bloco buscarBloco(Long blocoId) {
        return blocoRepository.findById(blocoId)
                .orElseThrow(() -> new RuntimeException("Bloco não encontrado com id: " + blocoId));
    }

    private void validarLamina(Lamina lamina) {
        if (lamina.getCorLamina() == null) {
            throw new IllegalArgumentException("A cor da lâmina é obrigatória.");
        }
        if (lamina.getPadraoLamina() == null) {
            throw new IllegalArgumentException("O padrão da lâmina é obrigatório.");
        }
        if (lamina.getPosicaoLamina() == null) {
            throw new IllegalArgumentException("A posição da lâmina é obrigatória.");
        }
        if (lamina.getBloco() == null || lamina.getBloco().getId() == null) {
            throw new IllegalArgumentException("O bloco associado é obrigatório.");
        }
    }
}