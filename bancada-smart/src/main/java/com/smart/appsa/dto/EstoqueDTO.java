package com.smart.appsa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Posição do estoque de blocos")
public record EstoqueDTO (
    @Schema(description = "ID interno") Long id,
    @Schema(description = "Posição no estoque (1–28)", example = "1") Integer posicao,
    @Schema(description = "Cor do bloco nessa posição (valor inteiro do enum CorBloco; 0 = posição vazia)", example = "0") Integer cor

){}