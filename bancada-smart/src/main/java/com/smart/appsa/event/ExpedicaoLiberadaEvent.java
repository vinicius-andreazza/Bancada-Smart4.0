package com.smart.appsa.event;

import org.springframework.context.ApplicationEvent;

public class ExpedicaoLiberadaEvent extends ApplicationEvent {

    private final int posicao;

    public ExpedicaoLiberadaEvent(Object source, int posicao) {
        super(source);
        this.posicao = posicao;
    }

    public int getPosicao() { return posicao; }
}
