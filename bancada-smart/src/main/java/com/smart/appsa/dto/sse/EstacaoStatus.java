package com.smart.appsa.dto.sse;

import java.time.LocalDateTime;

import com.smart.appsa.model.enums.StatusEstacao;

import lombok.Builder;

@Builder
public record EstacaoStatus(
  String estacao,
  StatusEstacao status,
  LocalDateTime atualizadoEm
){}