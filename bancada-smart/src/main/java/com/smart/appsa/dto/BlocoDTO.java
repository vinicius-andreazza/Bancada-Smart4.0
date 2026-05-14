package com.smart.appsa.dto;

import java.util.List;

import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.CorBloco;

public record BlocoDTO(
    Long id,
    CorBloco vl_cor,
    Estoque posEstoque,
    Pedido pedido,
    List<Lamina> laminas
) {
    
}
