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
public class MontagemPlc {
    private boolean recebidoOP;

    private int numeroOP;
    private boolean cancelOP;
    private boolean finishOP;
    private boolean startOP;

    private boolean ocupado;
    private boolean aguardando;
    private boolean manual;
    private boolean emergencia;
}
