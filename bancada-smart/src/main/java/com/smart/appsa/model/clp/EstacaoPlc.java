package com.smart.appsa.model.clp;

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

    private boolean concluidoOP;

    public StatusEstacao getStatus() {
        if (cancelOP)  return StatusEstacao.CANCELADO;
        if (finishOP || concluidoOP)  return StatusEstacao.FINALIZADO;
        if (ocupado)   return StatusEstacao.OCUPADO;
        return StatusEstacao.START;
    }
}
