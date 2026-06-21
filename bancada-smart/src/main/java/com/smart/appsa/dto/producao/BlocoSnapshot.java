package com.smart.appsa.dto.producao;

import java.util.List;

import lombok.Builder;

@Builder
public record BlocoSnapshot(
    int corBloco,
    int posEstoque,
    List<LaminaSnapshot> laminas
) {
}