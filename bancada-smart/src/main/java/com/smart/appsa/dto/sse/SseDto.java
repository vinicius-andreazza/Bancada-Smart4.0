package com.smart.appsa.dto.sse;

import java.util.List;

import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.dto.ExpedicaoDTO;

import lombok.Builder;

@Builder
public record SseDto(
  Integer codPedidoAtual,
  String duracao,
  List<EstacaoStatus> estacaoStatus,
  List<EstoqueDTO> estoque,
  List<ExpedicaoDTO> expedicao
) {
    
}