package com.smart.appsa.service.clp;

import com.smart.appsa.config.ipconfig.ExpedicaoIp;
import com.smart.appsa.service.ExpedicaoService;
import com.smart.appsa.service.PedidoService;
import com.smart.appsa.service.ProducaoService;
import com.smart.appsa.service.clp.poller.PlcPoller;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.mapper.clp.ExpedicaoPlcMapper;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.plc.ExpedicaoPlc;

@Service
public class ExpedicaoComm {
    private final ExpedicaoIp expedicaoIp;
    private final ExpedicaoService expedicaoService;
    private final PedidoService pedidoService;
    private final ProducaoService producaoService;

    private final PlcPoller poller;

    private int opAntiga = 0;
    private int opCancelada = 0;

    private final PlcConnectionService plcConnectionService;

    private final ExpedicaoPlc expedicaoPlc;

    private static final int DELAY = 400;
    private static final int DB_EXPEDICAO = 9;
    private static final int OFFSET_STATUS_OP = 0;
    private static final int OFFSET_GERENCIAMENTO_EXPEDICAO = 2;
    private static final int OFFSET_POSICAO_GUARDAR = 4;

    public ExpedicaoComm(PlcConnectionService plcConnectionService,
                         ExpedicaoPlc expedicaoPlc, ExpedicaoService expedicaoService, PedidoService pedidoService,
                         ProducaoService producaoService, ExpedicaoIp expedicaoIp) {
        this.poller = new PlcPoller(plcConnectionService);
        this.plcConnectionService = plcConnectionService;
        this.expedicaoPlc = expedicaoPlc;
        this.expedicaoService = expedicaoService;
        this.pedidoService = pedidoService;
        this.producaoService = producaoService;
        this.expedicaoIp = expedicaoIp;
    }

    public void startComm() {
        poller.start(expedicaoIp.getIp(), DELAY, () -> {
            try {
                handleData(getConnector().readBlock(DB_EXPEDICAO, 0, 46));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void disconnect() { poller.stop(); }
    public boolean isConnected() { return poller.isConnected(); }


    private void handleData(byte[] data) {

        ExpedicaoPlcMapper.updateData(data, expedicaoPlc);

        validarOperacao();
        validarPosicaoGuardar();
        validarRecepcao();
        validarAdicao();
        validarRetirada();
        validarFinalizacaoOP();
        validarCancelamento();
        concluirPedido();

        atualizarExpedicao();

    }

    private void validarOperacao() {
        if (!expedicaoPlc.isStartOP() && !expedicaoPlc.isFinishOP() && !expedicaoPlc.isCancelOP()) {
            try {
                getConnector().writeBit(DB_EXPEDICAO, OFFSET_STATUS_OP, 0, false);
            } catch (Exception e) {
                System.out.println("ERRO: Atualização da Flag RecebidoOPExp [DB_EXPEDICAO:OFFSET_STATUS_OP.0] para FALSE");
            }
        }

        validarRecebido();
    }

    private void validarRecebido(){
        if (expedicaoPlc.isStartOP() && !expedicaoPlc.isRecebidoOP()) {
            try {
                getConnector().writeBit(DB_EXPEDICAO, OFFSET_STATUS_OP, 0, true);
            } catch (Exception e) {
                System.out.println("ERRO [startOP]: Atualização da Flag RecebidoOPExp [DB_EXPEDICAO:OFFSET_STATUS_OP.0] para TRUE");
            }
        }

        if (expedicaoPlc.isFinishOP() && !expedicaoPlc.isRecebidoOP()) {
            try {
                getConnector().writeBit(DB_EXPEDICAO, OFFSET_STATUS_OP, 0, true);
            } catch (Exception e) {
                System.out.println("ERRO [finishOP]: Atualização da Flag RecebidoOPExp [DB_EXPEDICAO:OFFSET_STATUS_OP.0] para TRUE");
            }
        }
    }

    private void validarPosicaoGuardar() {
        if (!expedicaoPlc.isPedirPosicao()) {
            try {
                getConnector().writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, 1, false);
                
            }catch(Exception e){
                System.out.println("ERRO [Pedir Posição]: Atualização da Flag IniciarGuardar [DB_EXPEDICAO:OFFSET_GERENCIAMENTO_EXPEDICAO.1] para FALSE");
            }
            return;
        }

        int posicaoExpedicaoSolicitada = buscarPrimeiraPosicaoLivre();

        try {
            getConnector().writeInt(DB_EXPEDICAO, OFFSET_POSICAO_GUARDAR, posicaoExpedicaoSolicitada);
        } catch (Exception e) {
            System.out.println("ERRO: Atualização da PosicaoGuardarExpedicao [DB_EXPEDICAO:OFFSET_POSICAO_GUARDAR]");
        }

        try {
            getConnector().writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, 1, true);
        } catch (Exception e) {
            System.out.println("ERRO [Pedir Posição]: Atualização da Flag IniciarGuardar [DB_EXPEDICAO:OFFSET_GERENCIAMENTO_EXPEDICAO.1] para TRUE");
        }
    }

    private void validarRecepcao() {
        if (!expedicaoPlc.isAdicionar() && !expedicaoPlc.isRemover()) {
            try {
                getConnector().writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, 0, false);
            } catch (Exception e) {
                System.out.println("ERRO: Atualização da Flag RecebidoExpedicao [DB_EXPEDICAO:OFFSET_GERENCIAMENTO_EXPEDICAO.0] para FALSE");
            }
        }
    }

    private void validarAdicao() {
        if (!expedicaoPlc.isAdicionar()) {
            return;
        }

        try {
            getConnector().writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, 0, true);
        } catch (Exception e) {
            System.out.println("ERRO [Adicionar Expedição]: Atualização da Flag RecebidoExpedicao [DB_EXPEDICAO:OFFSET_GERENCIAMENTO_EXPEDICAO.0] para TRUE");
        }

        int posicaoGuardar = expedicaoPlc.getPosicaoGuardar();
        if (posicaoGuardar <= 0) {
            return;
        }

        int offset = 6 + (posicaoGuardar - 1) * 2;
        System.out.println("Guardando Operacao em posicaoGuardarExp: " + posicaoGuardar);

        try {
            getConnector().writeInt(DB_EXPEDICAO, offset, expedicaoPlc.getOpGuardado());

            expedicaoService.assignPedidoByPosition(getPedidoByCod(expedicaoPlc.getOpGuardado()).getId(), posicaoGuardar);

        } catch (Exception e) {
            System.out.println("ERRO: Na tentativa de adicionar na Expedição");
            e.printStackTrace();
        }
    }

    private void validarRetirada() {
        if (!expedicaoPlc.isRemover()) {
            return;
        }

        try {
            getConnector().writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, 0, true); // coloca recebidoExpedicao em TRUE
        } catch (Exception e) {
            System.out.println("ERRO [Remover Expedição]: Atualização da Flag RecebidoExpedicao [DB_EXPEDICAO:OFFSET_GERENCIAMENTO_EXPEDICAO.0] para TRUE");
        }

        int posicaoRemovida = expedicaoPlc.getPosicaoRemovido();
        if (posicaoRemovida <= 0) {
            return;
        }

        int offset = 6 + (posicaoRemovida - 1) * 2;
        System.out.println("Removendo Operacao de posicaoRemovidoExpedicao: " + posicaoRemovida);

        try {
            getConnector().writeInt(DB_EXPEDICAO, offset, 0);
            expedicaoService.releasePosicao(Long.parseLong(posicaoRemovida+""));
        } catch (Exception e) {
            System.out.println("ERRO: Na tentativa de remover da Expedição");
            e.printStackTrace();
        }
    }

    private void validarFinalizacaoOP() {
        if (expedicaoPlc.getPosicaoGuardado() == expedicaoPlc.getPosicaoGuardar()
                && !expedicaoPlc.isOcupado()
                && expedicaoPlc.isFinishOP()) {
            updateStatusConcluido();
            System.out.println("Operação OP:" + expedicaoPlc.getOpGuardado() + " Finalizada.");
        }
    }

    private void concluirPedido(){
        if(((expedicaoPlc.isFinishOP() || !expedicaoPlc.isRecebidoOP()) && (expedicaoPlc.getOpGuardado()>0 && expedicaoPlc.getOpGuardado() != opAntiga))){
            opAntiga = expedicaoPlc.getOpGuardado();
            producaoService.concluirProducao(expedicaoPlc.getOpGuardado());
        }
    }

    private void validarCancelamento(){
        if(expedicaoPlc.isCancelOP() && expedicaoPlc.getOpGuardado() > 0
                && expedicaoPlc.getOpGuardado() != opCancelada){
            opCancelada = expedicaoPlc.getOpGuardado();
            producaoService.cancelarOuFalharProducao(expedicaoPlc.getOpGuardado(),
                    "cancelamento sinalizado pelo CLP de expedição (CancelOP)");
        }
    }

    private void atualizarExpedicao(){

        List<Expedicao> expedicao = expedicaoService.findAllExpedicao();
        expedicao.forEach(e -> {
            try {
                if(e.getPedido()!=null){
                    getConnector().writeInt(DB_EXPEDICAO, 6+((e.getPosicao()-1)*2), e.getPedido().getCodPedido());
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }

    private void updateStatusConcluido(){
        expedicaoPlc.setConcluidoOP(true);
    }

    private int buscarPrimeiraPosicaoLivre() {
        return expedicaoService.findFirstAvailable().getPosicao();
    }

    private Pedido getPedidoByCod(int codigo){
        return pedidoService.findPedidoByCodigo(codigo);
    }

    private PlcConnector getConnector() {
        return plcConnectionService.getConnection(expedicaoIp.getIp());
    }
}
