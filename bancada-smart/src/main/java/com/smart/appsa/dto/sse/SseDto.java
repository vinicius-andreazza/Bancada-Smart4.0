package com.smart.appsa.dto.sse;

import java.util.List;

import lombok.Builder;

@Builder
public record SseDto(
  Integer codPedidoAtual,
  String inicioPedido,
  List<EstacaoStatus> estacaoStatus

) {
    
}