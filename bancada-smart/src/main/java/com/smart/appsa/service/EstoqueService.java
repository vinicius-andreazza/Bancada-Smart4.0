package com.smart.appsa.service;

import com.smart.appsa.dto.EstoqueDTO;

public interface EstoqueService {
    void adicionarLamina(EstoqueDTO estoqueDTO);
    void removerLamina(EstoqueDTO estoqueDTO);
    int consultarQuantidade(int tipoPedido, int posicao);
}