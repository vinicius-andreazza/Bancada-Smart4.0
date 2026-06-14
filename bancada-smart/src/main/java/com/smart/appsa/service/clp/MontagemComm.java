package com.smart.appsa.service.clp;

import com.smart.appsa.config.ipconfig.MontagemIp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.mapper.clp.MontagemPlcMapper;
import com.smart.appsa.model.plc.MontagemPlc;
import com.smart.appsa.service.clp.reader.PlcReader;

@Service
public class MontagemComm {
    
    private final MontagemIp montagemIp;

    private PlcReader plcReader;

    private final PlcConnectionService plcConnectionService;

    private final MontagemPlc montagemPlc;

    private final ExecutorService processoPool = Executors.newSingleThreadExecutor();

    private static final int DELAY = 600;
    private static final int DB_MONTAGEM = 57;
    private static final int OFFSET_RECEBIDO_OP = 0;

    public MontagemComm(PlcConnectionService plcConnectionService, MontagemPlc montagemPlc, MontagemIp montagemIp) {
        this.plcConnectionService = plcConnectionService;
        this.montagemPlc = montagemPlc;
        this.montagemIp = montagemIp;
    }

    public void startComm() {
        this.plcReader = new PlcReader(plcConnectionService.getConnection(montagemIp.getIp()), "Montagem", DB_MONTAGEM, 0, 6,
                data -> handleData(data));
        processoPool.execute(plcReader);
    }

    public boolean isConnected() {
        return plcReader != null && 
           plcConnectionService.getConnection(montagemIp.getIp()).isConnected();
    }

    private void handleData(byte[] data) {

        MontagemPlcMapper.updateData(data, montagemPlc);

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
        if (!montagemPlc.isStartOP() && !montagemPlc.isFinishOP() && !montagemPlc.isCancelOP()) {
            try {
                getConnector().writeBit(DB_MONTAGEM, OFFSET_RECEBIDO_OP, 0, false); // coloca RecebidoOPPro em FALSE
            } catch (Exception ex) {
            }
        }
    }

    private void validarInicioDaOperacao() {
        if (montagemPlc.isStartOP() && !montagemPlc.isRecebidoOP())
        {
            try {
                getConnector().writeBit(DB_MONTAGEM, OFFSET_RECEBIDO_OP, 0, true); // coloca RecebidoOPPro em TRUE
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void validarFinalizacaoDaOperacao() {
        if (montagemPlc.isFinishOP() && !montagemPlc.isRecebidoOP()) {
            try {
                getConnector().writeBit(DB_MONTAGEM, OFFSET_RECEBIDO_OP, 0, true); // coloca RecebidoOPPro em TRUE
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private PlcConnector getConnector() {
        return plcConnectionService.getConnection(montagemIp.getIp());
    }


}
