package com.smart.appsa.dto;

import java.util.List;

import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.CorBloco;

import lombok.Builder;

@Builder
public record BlocoDTO(
    Long id,
    CorBloco vl_cor,
    Estoque posEstoque,
    Pedido pedido,
    List<LaminaDTO> laminas
) {
    
}
