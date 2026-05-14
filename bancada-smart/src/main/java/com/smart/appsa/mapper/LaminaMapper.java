package com.smart.appsa.mapper;

import com.smart.appsa.dto.LaminaDTO;
import com.smart.appsa.model.Lamina;

public interface LaminaMapper {

    public static LaminaDTO toDto(Lamina lamina){
        return LaminaDTO.builder().id(lamina.getId()).bloco(BlocoMapper.toDto(lamina.getBloco())).padraoLamina(lamina.getPadraoLamina()).posicaoLamina(lamina.getPosicaoLamina()).corLamina(lamina.getCorLamina()).build();
    }

    public static Lamina toEntity(LaminaDTO laminaDTO){
        return Lamina.builder().id(laminaDTO.id()).bloco(BlocoMapper.toEntity(laminaDTO.bloco())).corLamina(laminaDTO.corLamina()).padraoLamina(laminaDTO.padraoLamina()).posicaoLamina(laminaDTO.posicaoLamina()).build();
    }
}
