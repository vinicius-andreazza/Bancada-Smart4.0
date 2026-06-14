package com.smart.appsa.service.clp;

import com.smart.appsa.config.ipconfig.ExpedicaoIp;
import com.smart.appsa.service.ExpedicaoService;
import com.smart.appsa.service.PedidoService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.mapper.PedidoMapper;
import com.smart.appsa.mapper.clp.ExpedicaoPlcMapper;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.plc.ExpedicaoPlc;
import com.smart.appsa.service.clp.reader.PlcReader;

@Service
public class ExpedicaoComm {
    private final ExpedicaoIp expedicaoIp;
    private final ExpedicaoService expedicaoService;
    private final PedidoService pedidoService;

    private PlcReader plcReader;

    private final PlcConnectionService plcConnectionService;

    private final ExpedicaoPlc expedicaoPlc;

    private final ExecutorService expedicaoPool = Executors.newSingleThreadExecutor();

    private static final int DELAY = 600;
    private static final int DB_EXPEDICAO = 9;
    private static final int OFFSET_STATUS_OP = 0;
    private static final int OFFSET_GERENCIAMENTO_EXPEDICAO = 2;
    private static final int OFFSET_POSICAO_GUARDAR = 4;

    public ExpedicaoComm(PlcConnectionService plcConnectionService,
                         ExpedicaoPlc expedicaoPlc, ExpedicaoService expedicaoService, PedidoService pedidoService, ExpedicaoIp expedicaoIp) {
        this.plcConnectionService = plcConnectionService;
        this.expedicaoPlc = expedicaoPlc;
        this.expedicaoService = expedicaoService;
        this.pedidoService = pedidoService;
        this.expedicaoIp = expedicaoIp;
    }

    public void startComm() {
        this.plcReader = new PlcReader(plcConnectionService.getConnection(expedicaoIp.getIp()), "Expedicao", DB_EXPEDICAO, 0, 46,
                data -> handleData(data));
        expedicaoPool.execute(plcReader);
    }

    private void handleData(byte[] data) {

        ExpedicaoPlcMapper.updateData(data, expedicaoPlc);

        validarOperacao();
        validarPosicaoGuardar();
        validarRecepcao();
        validarAdicao();
        validarRetirada();
        validarFinalizacaoOP();

        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            System.out.println("Operação OP:" + expedicaoPlc.getOpGuardado() + " Finalizada.");
        }
    }

    private int buscarPrimeiraPosicaoLivre() {
        return expedicaoService.findFirstAvailable().getPosicao();
    }

    private Pedido getPedidoByCod(int codigo){
        return PedidoMapper.toEntity(pedidoService.findByCodigo(expedicaoPlc.getOpGuardado()));
    }

    private PlcConnector getConnector() {
        return plcConnectionService.getConnection(expedicaoIp.getIp());
    }
}
