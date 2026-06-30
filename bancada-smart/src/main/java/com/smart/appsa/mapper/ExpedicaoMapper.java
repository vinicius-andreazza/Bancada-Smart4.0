package com.smart.appsa.mapper;

import com.smart.appsa.dto.ExpedicaoDTO;
import com.smart.appsa.model.Expedicao;

public interface ExpedicaoMapper {
    public static ExpedicaoDTO toDto(Expedicao expedicao){
        return ExpedicaoDTO.builder().id(expedicao.getId()).posicao(expedicao.getPosicao()).build();
    }
    public static Expedicao toEntity(ExpedicaoDTO expedicaoDTO){
        return Expedicao.builder().id(expedicaoDTO.id()).pedido(PedidoMapper.toEntity(expedicaoDTO.pedido())).posicao(expedicaoDTO.posicao()).build();
    }
}
