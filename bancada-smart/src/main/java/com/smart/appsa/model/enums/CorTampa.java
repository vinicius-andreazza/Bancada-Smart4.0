package com.smart.appsa.model.enums;

public enum CorTampa {
    PRETO(0),
    VERMELHO(1),
    AZUL(3);

    int value;

    private CorTampa(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
