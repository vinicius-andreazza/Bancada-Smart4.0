package com.smart.appsa.dto;

import com.smart.appsa.model.enums.CorLamina;
import com.smart.appsa.model.enums.PadraoLamina;
import com.smart.appsa.model.enums.PosicaoLamina;

import lombok.Builder;

@Builder
public record LaminaDTO(
    Long id,
    CorLamina corLamina,
    PadraoLamina padraoLamina,
    PosicaoLamina posicaoLamina,
    BlocoDTO bloco
) {
    
}
