package com.smart.appsa.service.clp.estacao;

import com.smart.appsa.config.ipconfig.MontagemIp;
import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.mapper.clp.MontagemPlcMapper;
import com.smart.appsa.model.clp.MontagemPlc;
import com.smart.appsa.service.clp.poller.PlcPoller;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MontagemComm {

    private final MontagemIp montagemIp;

    private final PlcPoller poller;

    private final PlcConnectionService plcConnectionService;

    private final MontagemPlc montagemPlc;

    private static final int DELAY = 600;
    private static final int DB_MONTAGEM = 57;
    private static final int OFFSET_RECEBIDO_OP = 0;

    public MontagemComm(PlcConnectionService plcConnectionService, MontagemPlc montagemPlc, MontagemIp montagemIp) {
        this.poller = new PlcPoller(plcConnectionService);
        this.plcConnectionService = plcConnectionService;
        this.montagemPlc = montagemPlc;
        this.montagemIp = montagemIp;
    }

    public void startComm() {
        poller.start(montagemIp.getIp(), DELAY, () -> {
            try {
                handleData(getConnector().readBlock(DB_MONTAGEM, 0, 8));
            } catch (Exception e) {
                log.error("Erro na leitura da montagem: {}", montagemIp.getIp(), e);
            }
        });
    }

    public void disconnect() {
        poller.stop();
    }

    public boolean isConnected() {
        return poller.isConnected();
    }

    private void handleData(byte[] data) {

        MontagemPlcMapper.updateData(data, montagemPlc);

        validarOperacaoPelasFlags();
        validarInicioDaOperacao();
        validarFinalizacaoDaOperacao();

    }

    private void validarOperacaoPelasFlags() {
        if (!montagemPlc.isStartOP() && !montagemPlc.isFinishOP() && !montagemPlc.isCancelOP()) {
            try {
                getConnector().writeBit(DB_MONTAGEM, OFFSET_RECEBIDO_OP, 0, false); // coloca RecebidoOPPro em FALSE
            } catch (Exception ex) {
                log.error(
                        "Atualização da Flag RecebidoOPMontagem [{}:{}.0] para FALSE",
                        DB_MONTAGEM,
                        OFFSET_RECEBIDO_OP,
                        ex);
            }
        }
    }

    private void validarInicioDaOperacao() {
        if (montagemPlc.isStartOP() && !montagemPlc.isRecebidoOP()) {
            try {
                getConnector().writeBit(DB_MONTAGEM, OFFSET_RECEBIDO_OP, 0, true); // coloca RecebidoOPPro em TRUE
            } catch (Exception e) {
                log.error(
                        "[startOp]: Atualização da Flag RecebidoOPMontagem [{}:{}.0] para TRUE",
                        DB_MONTAGEM,
                        OFFSET_RECEBIDO_OP,
                        e);
            }
        }
    }

    private void validarFinalizacaoDaOperacao() {
        if (montagemPlc.isFinishOP() && !montagemPlc.isRecebidoOP()) {
            updateStatusConcluido();
            try {
                getConnector().writeBit(DB_MONTAGEM, OFFSET_RECEBIDO_OP, 0, true); // coloca RecebidoOPPro em TRUE
            } catch (Exception e) {
                log.error(
                        "[finishOp]: Atualização da Flag RecebidoOPMontagem [{}:{}.0] para TRUE",
                        DB_MONTAGEM,
                        OFFSET_RECEBIDO_OP,
                        e);
            }
        }
    }

    private void updateStatusConcluido() {
        montagemPlc.setConcluidoOP(true);
    }

    private PlcConnector getConnector() {
        return plcConnectionService.getConnection(montagemIp.getIp());
    }

}
