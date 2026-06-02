package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.EnumeratedValue;

public enum TipoPedido {
    SIMPLES(1),
    DUPLO(2),
    TRIPLO(3);

    @EnumeratedValue
    int value;

    private TipoPedido(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static TipoPedido fromValue(int value) {
        for (TipoPedido tipo : values()) {
            if (tipo.value == value) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de pedido inválido: " + value);
    }

}
