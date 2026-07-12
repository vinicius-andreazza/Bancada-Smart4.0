package com.smart.appsa.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "IPs das estações CLP e endpoint do seletor de tampa")
public record CommDto(
    @Schema(description = "IP do CLP da estação Estoque", example = "192.168.0.10") String estoqueIp,
    @Schema(description = "IP do CLP da estação Processo", example = "192.168.0.11") String processoIp,
    @Schema(description = "IP do CLP da estação Montagem", example = "192.168.0.12") String montagemIp,
    @Schema(description = "IP do CLP da estação Expedição", example = "192.168.0.13") String expedicaoIp,
    @Schema(description = "URL do endpoint REST do seletor de tampa (ESP/Arduino)", example = "http://192.168.0.20:8080") String endpointSeletorTampa
) {

}
