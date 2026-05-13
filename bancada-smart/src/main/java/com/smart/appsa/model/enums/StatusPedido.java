package com.smart.appsa.model.enums;

import jakarta.persistence.EnumeratedValue;

public enum StatusPedido {
    PENDENTE(1),
    PRODUCAO(2),
    CONCLUIDO(3);

    @EnumeratedValue
    int value;

    private StatusPedido(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
