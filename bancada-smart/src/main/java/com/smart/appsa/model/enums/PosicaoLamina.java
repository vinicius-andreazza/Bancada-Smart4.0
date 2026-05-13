package com.smart.appsa.model.enums;

import jakarta.persistence.EnumeratedValue;

public enum PosicaoLamina {
    ESQUERDA(1),
    DIREITA(3),
    FRENTE(2);

    @EnumeratedValue
    int value;

    private PosicaoLamina(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
