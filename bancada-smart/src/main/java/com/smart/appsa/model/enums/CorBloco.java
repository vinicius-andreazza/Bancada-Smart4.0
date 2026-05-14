package com.smart.appsa.model.enums;

import jakarta.persistence.EnumeratedValue;

public enum CorBloco {
    NENHUM(0),
    PRETO(1),
    VERMELHO(2),
    AZUL(3);

    @EnumeratedValue
    int value;

    private CorBloco(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
