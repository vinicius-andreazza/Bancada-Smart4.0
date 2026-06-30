package com.smart.appsa.mapper;

import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.model.Estoque;

public interface EstoqueMapper {
    public static EstoqueDTO toDto(Estoque estoque){
        return EstoqueDTO.builder().posicao(estoque.getPosicao()).cor(estoque.getCor()).build();
    }

    public static Estoque toEntity(EstoqueDTO estoqueDTO){
        return Estoque.builder().cor(estoqueDTO.cor()).posicao(estoqueDTO.posicao()).build();
    }
}
