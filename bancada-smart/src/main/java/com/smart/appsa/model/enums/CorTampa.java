package com.smart.appsa.model.enums;

import jakarta.persistence.EnumeratedValue;

public enum CorTampa {
    PRETO(1),
    VERMELHO(2),
    AZUL(3);

    @EnumeratedValue
    int value;

    private CorTampa(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
