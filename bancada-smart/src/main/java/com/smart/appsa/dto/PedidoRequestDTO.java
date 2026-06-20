package com.smart.appsa.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;

import lombok.Builder;
@Builder
public record PedidoRequestDTO(
        Long id,
        String clpIp,
        Integer codPedido,
        StatusPedido status,
        TipoPedido tipoPedido,
        CorTampa corTampa,
        LocalDateTime dataEntrada,
        LocalDateTime dataInicio,
        LocalDateTime dataCriacao,
        Integer idExpedicao,
        List<BlocoDTO> blocos
) {

}
