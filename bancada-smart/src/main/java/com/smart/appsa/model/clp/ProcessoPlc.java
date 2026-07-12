package com.smart.appsa.model.clp;

import org.springframework.stereotype.Component;

import com.smart.appsa.model.enums.StatusEstacao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Component
public class ProcessoPlc extends EstacaoPlc {
    private boolean emProducao;

    @Override
    public StatusEstacao getStatus() {
        if (cancelOP)  return StatusEstacao.CANCELADO;
        if (finishOP || concluidoOP)  return StatusEstacao.FINALIZADO;
        if (ocupado || emProducao)   return StatusEstacao.OCUPADO;
        return StatusEstacao.START;
    }
}
