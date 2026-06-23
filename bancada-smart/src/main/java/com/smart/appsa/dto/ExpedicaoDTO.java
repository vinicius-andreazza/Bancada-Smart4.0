package com.smart.appsa.dto;

import com.smart.appsa.dto.response.PedidoResponseDTO;

import lombok.Builder;

@Builder
public record ExpedicaoDTO (
    Long id,
    Integer posicao,
    PedidoResponseDTO pedido
){}
