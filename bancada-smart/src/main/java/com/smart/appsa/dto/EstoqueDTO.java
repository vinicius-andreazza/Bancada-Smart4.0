package com.smart.appsa.dto;

import lombok.Builder;

@Builder
public record EstoqueDTO (
    Integer posicao,
    Integer cor

){}