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

    public PlcReader(PlcConnector plcConnector, String nome, int db, int offset, int size, Consumer<byte[]> data) {
        this.plcConnector = plcConnector;
        this.nome = nome;
        this.db = db;
        this.offset = offset;
        this.size = size;
        this.data = data;
    }

    @Override
    public void run() {
        if(plcConnector!=null){
            try {
                data.accept(plcConnector.readBlock(db, offset, size));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
}
