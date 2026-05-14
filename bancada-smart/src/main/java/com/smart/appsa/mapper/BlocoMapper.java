package com.smart.appsa.mapper;

import com.smart.appsa.dto.BlocoDTO;
import com.smart.appsa.model.Bloco;

public interface BlocoMapper {

    public static BlocoDTO toDto(Bloco bloco){
        return BlocoDTO.builder().id(bloco.getId()).posEstoque(bloco.getPosEstoque()).vl_cor(bloco.getVl_cor()).laminas(bloco.getLaminas().stream().map(l -> LaminaMapper.toDto(l)).toList()).pedido(bloco.getPedido()).build();
    }

    public static Bloco toEntity(BlocoDTO blocoDTO){
        return Bloco.builder().id(blocoDTO.id()).posEstoque(blocoDTO.posEstoque()).vl_cor(blocoDTO.vl_cor()).laminas(blocoDTO.laminas().stream().map(l -> LaminaMapper.toEntity(l)).toList()).pedido(blocoDTO.pedido()).build();
    }
    
}
