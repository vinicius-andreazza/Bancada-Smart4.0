package com.smart.appsa.mapper;

import com.smart.appsa.dto.PedidoRequestDTO;
import com.smart.appsa.dto.PedidoResponseDTO;
import com.smart.appsa.model.Pedido;
 
public class PedidoMapper {
 
    private PedidoMapper() {}
 
    public static Pedido toEntity(PedidoRequestDTO dto) {
        return Pedido.builder()
                .id(dto.id())
                .codPedido(dto.codPedido())
                .status(dto.status())
                .tipoPedido(dto.tipoPedido())
                .corTampa(dto.corTampa())
                .dataEntrada(dto.dataEntrada())
                .blocos(dto.blocos().stream().map(b -> BlocoMapper.toEntity(b)).toList())
                // expedicao é resolvida no service via idExpedicao
                .build();
    }
 
    public static PedidoResponseDTO toResponse(Pedido pedido) {
        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .codPedido(pedido.getCodPedido())
                .dataCriacao(pedido.getDataCriacao())
                .status(pedido.getStatus())
                .tipoPedido(pedido.getTipoPedido())
                .corTampa(pedido.getCorTampa())
                .dataEntrada(pedido.getDataEntrada())
                .idExpedicao(pedido.getPosExpedicao())
                .blocos(pedido.getBlocos().stream().map(b -> BlocoMapper.toDto(b)).toList())
                .build();
    }
 

}