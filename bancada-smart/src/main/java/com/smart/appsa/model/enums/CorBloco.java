package com.smart.appsa.model.enums;

import jakarta.persistence.EnumeratedValue;

public enum CorBloco {
    PRETO(0),
    VERMELHO(1),
    AZUL(2);

    @EnumeratedValue
    int value;

    private CorBloco(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
