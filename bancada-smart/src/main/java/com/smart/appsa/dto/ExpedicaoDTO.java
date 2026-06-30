package com.smart.appsa.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;

@Builder
public record ExpedicaoDTO (
    Long id,
    Integer posicao,
    @JsonIgnore
    PedidoRequestDTO pedido
){}
