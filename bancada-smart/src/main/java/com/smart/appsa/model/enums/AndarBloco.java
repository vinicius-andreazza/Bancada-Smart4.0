package com.smart.appsa.model.enums;

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

    public int getValue() {
        return value;
    }
}
