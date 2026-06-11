package com.smart.appsa.mapper.clp;

import com.smart.appsa.model.plc.MontagemPlc;

public interface MontagemPlcMapper {
    public static void updateData(byte[] data, MontagemPlc montagemPlc){
        montagemPlc.setRecebidoOP((data[0] & 0x01) != 0);

        montagemPlc.setNumeroOP(((data[2] & 0xFF) << 8) | (data[3] & 0xFF));
        montagemPlc.setCancelOP((data[4] & 0x01) != 0);
        montagemPlc.setFinishOP((data[4] & 0x02) != 0);
        montagemPlc.setStartOP((data[4] & 0x04) != 0);

        montagemPlc.setOcupado((data[6] & 0x01) != 0);
        montagemPlc.setAguardando((data[6] & 0x02) != 0);
        montagemPlc.setManual((data[6] & 0x04) != 0);
        montagemPlc.setEmergencia((data[6] & 0x08) != 0);
    }
}
