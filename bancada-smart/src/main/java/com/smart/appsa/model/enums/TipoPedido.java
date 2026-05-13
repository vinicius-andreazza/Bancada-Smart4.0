package com.smart.appsa.model.enums;

import jakarta.persistence.EnumeratedValue;

public enum TipoPedido {
    SIMPLES(1),
    DUPLO(2),
    TRIPLO(3);

    @EnumeratedValue
    int value;

    private TipoPedido(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
