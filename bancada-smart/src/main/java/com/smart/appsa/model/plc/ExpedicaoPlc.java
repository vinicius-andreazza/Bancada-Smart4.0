package com.smart.appsa.model.plc;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class ExpedicaoPlc {

    private boolean recebidoOp;

    private boolean recebidoExpedicao;
    private boolean iniciarGuardar;
    private int posicaoGuardar;

    private int[] pedidosExpedicao;

    private int numeroOP;
    private boolean cancelOP;
    private boolean finishOP;
    private boolean startOP;

    private boolean ocupado;
    private boolean aguardando;
    private boolean manual;
    private boolean emergencia;

    private boolean pedirPosicao;
    private int posicaoGuardado;
    private int posicaoRemovido;
    private boolean adicionar;
    private boolean remover;
    private int opGuardado;
}
