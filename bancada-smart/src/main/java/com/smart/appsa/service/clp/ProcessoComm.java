package com.smart.appsa.service.clp;

import com.smart.appsa.config.ipconfig.ProcessoIp;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.mapper.clp.ProcessoPlcMapper;
import com.smart.appsa.model.plc.ProcessoPlc;
import com.smart.appsa.service.clp.poller.PlcPoller;

@Service
public class ProcessoComm {

    private final ProcessoIp processoIp;

    private final PlcPoller poller;

    private final PlcConnectionService plcConnectionService;

    private final ProcessoPlc processoPlc;


    private static final int DELAY = 600;
    private static final int DB_PROCESSO = 2;
    private static final int OFFSET_RECEBIDO_OP = 0;

    public ProcessoComm(PlcConnectionService plcConnectionService, ProcessoPlc processoPlc, ProcessoIp processoIp) {
        this.poller = new PlcPoller(plcConnectionService);
        this.plcConnectionService = plcConnectionService;
        this.processoPlc = processoPlc;
        this.processoIp = processoIp;
    }

    public void startComm() {
        poller.start(processoIp.getIp(), DELAY, () -> {
            try {
                handleData(getConnector().readBlock(DB_PROCESSO, 0, 8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void disconnect() { poller.stop(); }
    public boolean isConnected() { return poller.isConnected(); }

    private void handleData(byte[] data) {

        ProcessoPlcMapper.updateData(data, processoPlc);

        validarOperacaoPelasFlags();
        validarInicioDaOperacao();
        validarFinalizacaoDaOperacao();
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
