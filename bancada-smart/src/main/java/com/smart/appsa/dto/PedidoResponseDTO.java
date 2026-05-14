package com.smart.appsa.dto;

import java.time.LocalDateTime;

import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;

import lombok.Builder;
@Builder
public record PedidoResponseDTO(
        Long id,
        String codPedido,
        LocalDateTime dataCriacao,
        StatusPedido status,
        TipoPedido tipoPedido,
        CorTampa corTampa,
        LocalDateTime dataEntrada,
        Expedicao expedicao
    ) {

}
