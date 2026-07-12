package com.smart.appsa.service.clp.poller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.smart.appsa.clpcomm.PlcConnectionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlcPoller {
    private final PlcConnectionService plcConnectionService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> tarefaAtual;
    private String ipConectado;

    public PlcPoller(PlcConnectionService plcConnectionService) {
        this.plcConnectionService = plcConnectionService;
    }

    public synchronized void start(String ip, int delayMs, Runnable leitura) {
        stop();
        if (ipConectado != null && !ipConectado.equals(ip)) {
            plcConnectionService.disconnect(ipConectado);
        }
        ipConectado = ip;
        tarefaAtual = scheduler.scheduleWithFixedDelay(leitura, 0, delayMs, TimeUnit.MILLISECONDS);
    }

    public synchronized void stop() {
        if (tarefaAtual != null) tarefaAtual.cancel(true);
    }

    public boolean isConnected() {
        return ipConectado != null && plcConnectionService.getConnection(ipConectado).isConnected();
    }
}
