package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.EnumeratedValue;

public enum PadraoLamina {
    NENHUM(0),
    CASA(1),
    NAVIO(2),
    ESTRELA(3);

    @EnumeratedValue
    int value;

    private PadraoLamina(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
    @JsonCreator
    public static PadraoLamina fromValue(int value) {
        for (PadraoLamina tipo : values()) {
            if (tipo.value == value) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Padrão da lamina inválido: " + value);
    }
}
