package com.smart.appsa.dto.producao;

import java.util.List;

import lombok.Builder;


@Builder
public record ProducaoSnapshot(
        Integer codPedido,
        int tipoPedido,
        int corTampa,
        Integer posExpedicao,
        List<BlocoSnapshot> blocos
) {}