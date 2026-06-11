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
public class ProcessoPlc {
    boolean recebidoOP;

    int numeroOP;
    boolean cancelOP;
    boolean finishOP;
    boolean startOP;


    boolean ocupado;
    boolean aguardando;
    boolean manual;
    boolean emergencia;
}
