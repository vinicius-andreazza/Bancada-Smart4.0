package com.smart.appsa.dto.response;

import lombok.Builder;

@Builder
public record CountStatus(
    Integer total,
    Integer pendentes,
    Integer producao,
    Integer concluidos,
    Integer cancelado
) {
}
