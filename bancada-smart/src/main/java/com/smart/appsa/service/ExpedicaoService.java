package com.smart.appsa.service;

import java.util.List;

import com.smart.appsa.dto.ExpedicaoDTO;
import com.smart.appsa.model.Expedicao;

public interface ExpedicaoService {

    Expedicao buscarPorId(Long id);
    List<Expedicao> listarTodas();
    Expedicao criarPosicao(ExpedicaoDTO dto);
    Expedicao atribuirPedido(Long expedicaoId, Long pedidoId);
    Expedicao liberarPosicao(Long expedicaoId);
    void removerPosicao(Long id);
}
