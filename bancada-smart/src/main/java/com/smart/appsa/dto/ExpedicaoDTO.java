package com.smart.appsa.dto;

public class ExpedicaoDTO {

    private Integer posicao;
    private Long pedidoId; // nullable — posição pode estar livre

    public ExpedicaoDTO() {}

    public ExpedicaoDTO(Integer posicao, Long pedidoId) {
        this.posicao = posicao;
        this.pedidoId = pedidoId;
    }

    public Integer getPosicao() { return posicao; }
    public void setPosicao(Integer posicao) { this.posicao = posicao; }

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
}
