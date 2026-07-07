package com.smart.appsa.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.smart.appsa.dto.BlocoDTO;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Dados para criação ou atualização de um pedido")
public record PedidoRequestDTO(
        @Schema(description = "ID do pedido (obrigatório apenas em operações de update)") Long id,
        @Schema(description = "IP do CLP (não utilizado atualmente)", hidden = true) String clpIp,
        @Schema(description = "Código de negócio do pedido", example = "42") Integer codPedido,
        @Schema(description = "Status do pedido") StatusPedido status,
        @Schema(description = "Tipo de pedido (define a estrutura de blocos)") TipoPedido tipoPedido,
        @Schema(description = "Cor da tampa do produto") CorTampa corTampa,
        @Schema(description = "Data/hora de envio para a bancada") LocalDateTime dataEntrada,
        @Schema(description = "Data/hora de início da produção") LocalDateTime dataInicio,
        @Schema(description = "Data/hora de criação do pedido") LocalDateTime dataCriacao,
        @Schema(description = "Posição na expedição alocada para este pedido") Integer idExpedicao,
        @Schema(description = "Lista de blocos que compõem o produto") List<BlocoDTO> blocos
) {

}
