package com.smart.appsa.model.plc;

import com.smart.appsa.model.enums.StatusEstacao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EstacaoPlc {
    private boolean recebidoOP;

    private int numeroOP;

    private boolean cancelOP;
    private boolean finishOP;
    private boolean startOP;
    private boolean ocupado;
    
    private boolean aguardando;
    private boolean manual;
    private boolean emergencia;

    public StatusEstacao getStatus() {
        if (cancelOP)  return StatusEstacao.CANCELADO;
        if (finishOP)  return StatusEstacao.FINALIZADO;
        if (ocupado)   return StatusEstacao.OCUPADO;
        return StatusEstacao.START;
    }
}
