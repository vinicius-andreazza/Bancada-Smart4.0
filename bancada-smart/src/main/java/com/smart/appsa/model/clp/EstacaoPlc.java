package com.smart.appsa.model.clp;

import com.smart.appsa.model.enums.StatusEstacao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EstacaoPlc {
    protected boolean recebidoOP;

    protected int numeroOP;

    protected boolean cancelOP;
    protected boolean finishOP;
    protected boolean startOP;
    protected boolean ocupado;
    
    protected boolean aguardando;
    protected boolean manual;
    protected boolean emergencia;

    protected boolean concluidoOP;

    public StatusEstacao getStatus() {
        if (cancelOP)  return StatusEstacao.CANCELADO;
        if (finishOP || concluidoOP)  return StatusEstacao.FINALIZADO;
        if (ocupado)   return StatusEstacao.OCUPADO;
        return StatusEstacao.START;
    }
}
