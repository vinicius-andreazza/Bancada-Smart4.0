package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.EnumeratedValue;

public enum CorLamina {
    VERMELHO(1),
    AZUL(2),
    AMARELO(3),
    VERDE(4),
    PRETO(5),
    BRANCO(6);

    @EnumeratedValue
    int value;

    private CorLamina(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static CorLamina fromValue(Object value) {

        int valor;

        if (value instanceof Number) {
            valor = ((Number) value).intValue();
        } else {
            valor = Integer.parseInt(value.toString());
        }

        for (CorLamina cor : values()) {
            if (cor.value == valor) {
                return cor;
            }
        }

        throw new IllegalArgumentException("Cor da lamina inválido: " + value);
    }
}
