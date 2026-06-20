package com.smart.appsa.dto;

import lombok.Builder;

@Builder
public record ExpedicaoDTO (
    Long id,
    Integer posicao,
    PedidoResponseDTO pedido
){}
