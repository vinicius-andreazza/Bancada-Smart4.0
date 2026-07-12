package com.smart.appsa.service.clp.estacao;

import com.smart.appsa.config.ReadMode;
import com.smart.appsa.config.ipconfig.ProcessoIp;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.mapper.clp.ProcessoPlcMapper;
import com.smart.appsa.model.clp.ProcessoPlc;
import com.smart.appsa.service.clp.poller.PlcPoller;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcessoComm {

    private final ReadMode readMode;

    private final ProcessoIp processoIp;

    private final PlcPoller poller;

    private final PlcConnectionService plcConnectionService;

    private final ProcessoPlc processoPlc;

    private static final int DELAY = 600;
    private static final int DB_PROCESSO = 2;
    private static final int OFFSET_RECEBIDO_OP = 0;

    public ProcessoComm(PlcConnectionService plcConnectionService, ProcessoPlc processoPlc, ProcessoIp processoIp, ReadMode readMode) {
        this.poller = new PlcPoller(plcConnectionService);
        this.plcConnectionService = plcConnectionService;
        this.processoPlc = processoPlc;
        this.processoIp = processoIp;
        this.readMode = readMode;
    }

    public void startComm() {
        poller.start(processoIp.getIp(), DELAY, () -> {
            try {
                handleData(getConnector().readBlock(DB_PROCESSO, 0, 8));
            } catch (Exception e) {
                log.error("Erro na leitura do processo: {}", processoIp.getIp(), e);
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

        ProcessoPlcMapper.updateData(data, processoPlc);
        if (!readMode.isReadMode()) {
            validarOperacaoPelasFlags();
            validarInicioDaOperacao();
            validarFinalizacaoDaOperacao();
            validarEmOperacao();
        }
    }

    private void validarOperacaoPelasFlags() {
        if (!processoPlc.isStartOP() && !processoPlc.isFinishOP() && !processoPlc.isCancelOP()) {
            try {
                getConnector().writeBit(DB_PROCESSO, OFFSET_RECEBIDO_OP, 0, false); // coloca RecebidoOPPro em FALSE
            } catch (Exception ex) {
                log.error(
                        "Atualização da Flag RecebidoOPProcesso [{}:{}.0] para FALSE",
                        DB_PROCESSO,
                        OFFSET_RECEBIDO_OP,
                        ex);
            }
        }
    }

    private void validarInicioDaOperacao() {
        if (processoPlc.isStartOP() && !processoPlc.isRecebidoOP()) {
            try {
                getConnector().writeBit(DB_PROCESSO, OFFSET_RECEBIDO_OP, 0, true); // coloca RecebidoOPPro em TRUE
            } catch (Exception e) {
                log.error(
                        "[startOp]: Atualização da Flag RecebidoOPProcesso [{}:{}.0] para TRUE",
                        DB_PROCESSO,
                        OFFSET_RECEBIDO_OP,
                        e);
            }
        }
    }

    private void validarFinalizacaoDaOperacao() {
        if (processoPlc.isFinishOP() && !processoPlc.isRecebidoOP()) {
            updateStatusConcluido();
            try {
                getConnector().writeBit(DB_PROCESSO, OFFSET_RECEBIDO_OP, 0, true); // coloca RecebidoOPPro em TRUE
            } catch (Exception e) {
                log.error(
                        "[finishOp]: Atualização da Flag RecebidoOPProcesso [{}:{}.0] para TRUE",
                        DB_PROCESSO,
                        OFFSET_RECEBIDO_OP,
                        e);
            }
        }
    }

    private void validarEmOperacao() {
        if (processoPlc.isOcupado()) {
            updateStatusProducao();
        }
    }

    private void updateStatusConcluido() {
        processoPlc.setConcluidoOP(true);
        processoPlc.setEmProducao(false);
    }

    private void updateStatusProducao() {
        processoPlc.setConcluidoOP(false);
        processoPlc.setEmProducao(true);
    }

    private PlcConnector getConnector() {
        return plcConnectionService.getConnection(processoIp.getIp());
    }

}
