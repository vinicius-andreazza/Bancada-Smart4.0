package com.smart.appsa.mapper.clp;

import com.smart.appsa.model.clp.ProcessoPlc;

public interface ProcessoPlcMapper {
    public static void updateData(byte[] data, ProcessoPlc processoPlc){
        processoPlc.setRecebidoOP((data[0] & 0x01) != 0);

        processoPlc.setNumeroOP(((data[2] & 0xFF) << 8) | (data[3] & 0xFF));
        processoPlc.setCancelOP((data[4] & 0x01) != 0);
        processoPlc.setFinishOP((data[4] & 0x02) != 0);
        processoPlc.setStartOP((data[4] & 0x04) != 0);

        processoPlc.setOcupado((data[6] & 0x01) != 0);
        processoPlc.setAguardando((data[6] & 0x02) != 0);
        processoPlc.setManual((data[6] & 0x04) != 0);
        processoPlc.setEmergencia((data[6] & 0x08) != 0);
    }
}
