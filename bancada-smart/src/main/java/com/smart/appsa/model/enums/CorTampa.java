package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static CorTampa fromValue(int value) {
        for (CorTampa tipo : values()) {
            if (tipo.value == value) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Cor da tampa inválido: " + value);
    }

}
