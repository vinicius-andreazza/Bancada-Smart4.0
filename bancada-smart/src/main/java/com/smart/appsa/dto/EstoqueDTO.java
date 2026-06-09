package com.smart.appsa.dto;

import lombok.Builder;

@Builder
public record EstoqueDTO (
    Long id,
    Integer posicao,
    Integer cor

){}