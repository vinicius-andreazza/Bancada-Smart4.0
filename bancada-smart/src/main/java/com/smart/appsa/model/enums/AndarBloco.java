package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.EnumeratedValue;

public enum AndarBloco {
    PRIMEIRO(1),
    SEGUNDO(2),
    TERCEIRO(3);

    @EnumeratedValue
    int value;

    private AndarBloco(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static AndarBloco fromValue(int value) {
        for (AndarBloco tipo : values()) {
            if (tipo.value == value) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Andar do bloco inválido: " + value);
    }
}
