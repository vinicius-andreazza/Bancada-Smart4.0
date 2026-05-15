package com.smart.appsa.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.AndarBloco;
import com.smart.appsa.model.enums.CorBloco;

import lombok.Builder;

@Builder
public record BlocoDTO(
    Long id,
    CorBloco vl_cor,
    Integer posEstoque,
    AndarBloco andar,
    @JsonIgnore
    Pedido pedido,
    List<LaminaDTO> laminas
) {
    
}
