package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.EnumeratedValue;

public enum CorBloco {
    NENHUM(0),
    AZUL(1),
    PRETO(2),
    VERMELHO(3);

    @EnumeratedValue
    int value;

    private CorBloco(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static CorBloco fromValue(int value) {
        for (CorBloco tipo : values()) {
            if (tipo.value == value) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Cor do bloco inválido: " + value);
    }
}
