package com.smart.appsa.service.clp.reader;

import java.util.function.Consumer;

import com.smart.appsa.clpcomm.PlcConnector;

public class PlcReader implements Runnable {

    private final PlcConnector plcConnector;
    private final String nome;
    private final int db;
    private final int offset;
    private final int size;
    private final Consumer<byte[]> data;
    private final int DELAY;

    public PlcReader(PlcConnector plcConnector, String nome, int db, int offset, int size, Consumer<byte[]> data, int delay) {
        this.plcConnector = plcConnector;
        this.nome = nome;
        this.db = db;
        this.offset = offset;
        this.size = size;
        this.data = data;
        this.DELAY = delay;
    }

    @Override
    public void run() {
        while(plcConnector!=null){
            try {
                data.accept(plcConnector.readBlock(db, offset, size));
                Thread.sleep(DELAY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
}
