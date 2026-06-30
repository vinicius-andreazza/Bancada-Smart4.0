package com.smart.appsa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.stereotype.Component;

@Component
public class FilaProducao {

    private final ConcurrentLinkedDeque<Integer> fila = new ConcurrentLinkedDeque<>();

    public void adicionar(int codPedido) {
        fila.addLast(codPedido);
    }

    public Integer poll() {
        return fila.pollFirst();
    }

    public boolean remover(int codPedido) {
        return fila.remove(codPedido);
    }

    public List<Integer> listar() {
        return new ArrayList<>(fila);
    }
}
