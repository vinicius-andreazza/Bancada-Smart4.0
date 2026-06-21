package com.smart.appsa.dto.producao;

import lombok.Builder;

@Builder
public record LaminaSnapshot(
    int corLamina,
    int padraoLamina
) {
}