package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static PosicaoLamina fromValue(int value) {
        for (PosicaoLamina tipo : values()) {
            if (tipo.value == value) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Posição da lamina inválido: " + value);
    }
}
