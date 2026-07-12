package com.smart.appsa.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Contagem de pedidos agrupada por status")
public record CountStatus(
    @Schema(description = "Total de pedidos") Integer total,
    @Schema(description = "Pedidos com status PENDENTE") Integer pendentes,
    @Schema(description = "Pedidos com status PRODUCAO") Integer producao,
    @Schema(description = "Pedidos com status CONCLUIDO") Integer concluidos,
    @Schema(description = "Pedidos com status CANCELADO") Integer cancelado
) {
}
