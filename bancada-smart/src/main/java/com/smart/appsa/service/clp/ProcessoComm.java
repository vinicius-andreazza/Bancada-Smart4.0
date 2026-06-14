package com.smart.appsa.service.clp;

import com.smart.appsa.config.ipconfig.ProcessoIp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.mapper.clp.ProcessoPlcMapper;
import com.smart.appsa.model.plc.ProcessoPlc;
import com.smart.appsa.service.clp.reader.PlcReader;

@Service
public class ProcessoComm {

    private final ProcessoIp processoIp;

    private PlcReader plcReader;

    private final PlcConnectionService plcConnectionService;

    private final ProcessoPlc processoPlc;

    private final ExecutorService processoPool = Executors.newSingleThreadExecutor();

    private static final int DELAY = 600;
    private static final int DB_PROCESSO = 2;
    private static final int OFFSET_RECEBIDO_OP = 0;

    public ProcessoComm(PlcConnectionService plcConnectionService, ProcessoPlc processoPlc, ProcessoIp processoIp) {
        this.plcConnectionService = plcConnectionService;
        this.processoPlc = processoPlc;
        this.processoIp = processoIp;
    }

    public void startComm() {
        this.plcReader = new PlcReader(plcConnectionService.getConnection(processoIp.getIp()), "Processo", DB_PROCESSO, 0, 6,
                data -> handleData(data));
        processoPool.execute(plcReader);
    }

    public boolean isConnected() {
        return plcReader != null && 
           plcConnectionService.getConnection(processoIp.getIp()).isConnected();
    }

    private void handleData(byte[] data) {

        ProcessoPlcMapper.updateData(data, processoPlc);

        validarOperacaoPelasFlags();
        validarInicioDaOperacao();
        validarFinalizacaoDaOperacao();

        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void validarOperacaoPelasFlags() {
        if (!processoPlc.isStartOP() && !processoPlc.isFinishOP() && !processoPlc.isCancelOP()) {
            try {
                getConnector().writeBit(DB_PROCESSO, OFFSET_RECEBIDO_OP, 0, false); // coloca RecebidoOPPro em FALSE
            } catch (Exception ex) {
            }
        }
    }

    private void validarInicioDaOperacao() {
        if (processoPlc.isStartOP() && !processoPlc.isRecebidoOP())
        {
            try {
                getConnector().writeBit(DB_PROCESSO, OFFSET_RECEBIDO_OP, 0, true); // coloca RecebidoOPPro em TRUE
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void validarFinalizacaoDaOperacao() {
        if (processoPlc.isFinishOP() && !processoPlc.isRecebidoOP()) {
            try {
                getConnector().writeBit(DB_PROCESSO, OFFSET_RECEBIDO_OP, 0, true); // coloca RecebidoOPPro em TRUE
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private PlcConnector getConnector() {
        return plcConnectionService.getConnection(processoIp.getIp());
    }


}
