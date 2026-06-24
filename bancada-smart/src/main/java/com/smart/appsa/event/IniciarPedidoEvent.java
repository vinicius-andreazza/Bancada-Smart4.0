package com.smart.appsa.event;

import org.springframework.context.ApplicationEvent;

import com.smart.appsa.model.Pedido;

public class IniciarPedidoEvent extends ApplicationEvent {

    private Pedido pedido;

    public IniciarPedidoEvent(Object source, Pedido pedido) {
        super(source);
        this.pedido =pedido;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    
}
