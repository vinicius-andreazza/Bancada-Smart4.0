package com.smart.appsa.model.plc;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Component
public class ExpedicaoPlc extends EstacaoPlc {

    private boolean recebidoExpedicao;
    private boolean iniciarGuardar;
    private int posicaoGuardar;

    private int[] pedidosExpedicao;

    private boolean pedirPosicao;
    private int posicaoGuardado;
    private int posicaoRemovido;
    private boolean adicionar;
    private boolean remover;
    private int opGuardado;
}
