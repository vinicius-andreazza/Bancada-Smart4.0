package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.EnumeratedValue;

public enum StatusPedido {
    PENDENTE(1),
    PRODUCAO(2),
    CONCLUIDO(3);

    @EnumeratedValue
    int value;

    private StatusPedido(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static StatusPedido fromValue(int value) {
        for (StatusPedido tipo : values()) {
            if (tipo.value == value) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + value);
    }

}
