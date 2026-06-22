package com.smart.appsa.event;

import org.springframework.context.ApplicationEvent;

public class ExpedicaoReservadaEvent extends ApplicationEvent {

    private final int posicao;
    private final int codPedido;

    public ExpedicaoReservadaEvent(Object source, int posicao, int codPedido) {
        super(source);
        this.posicao = posicao;
        this.codPedido = codPedido;
    }

    public int getPosicao()   { return posicao; }
    public int getCodPedido() { return codPedido; }
}
