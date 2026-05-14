package com.smart.appsa.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;

import lombok.Builder;
@Builder
public record PedidoRequestDTO(
        String codPedido,
        StatusPedido status,
        TipoPedido tipoPedido,
        CorTampa corTampa,
        LocalDateTime dataEntrada,
        Long idExpedicao,
        List<Bloco> blocos
) {

}
