package com.smart.appsa.mapper;

import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
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
                .dataInicio(dto.dataInicio())
                .dataCriacao(dto.dataCriacao())
                .blocos(dto.blocos().stream().map(b -> BlocoMapper.toEntity(b)).toList())
                .build();
    }

    public static Pedido toEntity(PedidoResponseDTO dto) {
        return Pedido.builder()
                .id(dto.id())
                .codPedido(dto.codPedido())
                .status(dto.status())
                .tipoPedido(dto.tipoPedido())
                .corTampa(dto.corTampa())
                .dataEntrada(dto.dataEntrada())
                .dataInicio(dto.dataInicio())
                .dataCriacao(dto.dataCriacao())
                .blocos(dto.blocos().stream().map(b -> BlocoMapper.toEntity(b)).toList())
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
                .dataInicio(pedido.getDataInicio())
                .idExpedicao(pedido.getPosExpedicao())
                .blocos(pedido.getBlocos() == null ? null : pedido.getBlocos().stream().map(b -> BlocoMapper.toDto(b)).toList())
                .build();
    }
 

}