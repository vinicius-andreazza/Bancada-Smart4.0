package com.smart.appsa.model.plc;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Component
public class EstoquePlc extends EstacaoPlc {

    private int corAndar1;
    private int posEstoqueAndar1;
    private int corLamina1Andar1;
    private int corLamina2Andar1;
    private int corLamina3Andar1;
    private int padraoLamina1Andar1;
    private int padraoLamina2Andar1;
    private int padraoLamina3Andar1;
    private int processamentoAndar1;

    private int corAndar2;
    private int posEstoqueAndar2;
    private int corLamina1Andar2;
    private int corLamina2Andar2;
    private int corLamina3Andar2;
    private int padraoLamina1Andar2;
    private int padraoLamina2Andar2;
    private int padraoLamina3Andar2;
    private int processamentoAndar2;

    private int corAndar3;
    private int posEstoqueAndar3;
    private int corLamina1Andar3;
    private int corLamina2Andar3;
    private int corLamina3Andar3;
    private int padraoLamina1Andar3;
    private int padraoLamina2Andar3;
    private int padraoLamina3Andar3;
    private int processamentoAndar3;

    private int numeroPedido;
    private int andares;
    private int posExpedicao;

    private boolean iniciarPedido;
    private boolean recebidoEstoque;
    private boolean iniciarGuardar;
    private int posicaoGuardar;

    private byte[] posicoes;

    private boolean pedirPosicao;
    private int posicaoEstoque;
    private boolean adicionarEstoque;
    private boolean removerEstoque;
    private boolean retornoEstoqueCheio;
    private int corGuardarEstoque;
}
