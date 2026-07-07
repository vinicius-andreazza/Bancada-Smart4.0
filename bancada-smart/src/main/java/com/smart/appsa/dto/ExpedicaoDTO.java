package com.smart.appsa.dto;

import com.smart.appsa.dto.response.PedidoResponseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Posição de expedição da bancada")
public record ExpedicaoDTO (
    @Schema(description = "ID interno") Long id,
    @Schema(description = "Posição na expedição (1–12)", example = "1") Integer posicao,
    @Schema(description = "Pedido alocado nessa posição (null = posição livre)") PedidoResponseDTO pedido
){}
