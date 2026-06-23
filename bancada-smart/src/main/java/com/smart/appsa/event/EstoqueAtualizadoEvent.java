package com.smart.appsa.event;

import org.springframework.context.ApplicationEvent;

public class EstoqueAtualizadoEvent extends ApplicationEvent {

    private final int posicao;
    private final int cor;

    public EstoqueAtualizadoEvent(Object source, int posicao, int cor) {
        super(source);
        this.posicao = posicao;
        this.cor = cor;
    }

    public int getPosicao() { return posicao; }
    public int getCor()     { return cor; }
}
