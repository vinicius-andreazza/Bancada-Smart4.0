package com.smart.appsa.service;

import java.util.List;
import com.smart.appsa.model.Lamina;

public interface LaminaService {
    Lamina buscarLaminaPorId(Long id);
    List<Lamina> listarLaminasPorBloco(Long blocoId);
    Lamina criarLamina(Lamina lamina);
    Lamina atualizarLamina(Long id, Lamina lamina);
    void deletarLamina(Long id);
}