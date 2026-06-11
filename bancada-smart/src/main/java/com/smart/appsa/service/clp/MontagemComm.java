package com.smart.appsa.service.clp;

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
    
    private PlcReader plcReader;

    private final PlcConnectionService plcConnectionService;

    private final MontagemPlc montagemPlc;

    private String ip = "10.74.241.30";

    private final ExecutorService processoPool = Executors.newSingleThreadExecutor();

    private static final int DELAY = 600;
    private static final int DB_MONTAGEM = 57;
    private static final int OFFSET_RECEBIDO_OP = 0;

    public MontagemComm(PlcConnectionService plcConnectionService, MontagemPlc montagemPlc) {
        this.plcConnectionService = plcConnectionService;
        this.montagemPlc = montagemPlc;
    }

    public void startComm(String ip) {
        this.plcReader = new PlcReader(plcConnectionService.getConnection(ip), "Montagem", DB_MONTAGEM, 0, 6,
                data -> handleData(data, ip));
        processoPool.execute(plcReader);
    }

    private void handleData(byte[] data, String ip) {
        this.ip = ip;

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
        return plcConnectionService.getConnection(ip);
    }


}
