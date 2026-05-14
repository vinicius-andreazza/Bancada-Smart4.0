package com.smart.appsa.dto;

public class EstoqueDTO {
    private int tipoPedido;
    private int posicao;
    private int quantidade;

    // Construtores
    public EstoqueDTO() {}

    public EstoqueDTO(int tipoPedido, int posicao, int quantidade) {
        this.tipoPedido = tipoPedido;
        this.posicao = posicao;
        this.quantidade = quantidade;
    }

    // Getters e Setters
    public int getTipoPedido() {
        return tipoPedido;
    }

    public void setTipoPedido(int tipoPedido) {
        this.tipoPedido = tipoPedido;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}