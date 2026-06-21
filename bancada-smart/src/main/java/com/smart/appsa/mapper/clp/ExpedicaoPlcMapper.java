package com.smart.appsa.mapper.clp;

import com.smart.appsa.model.clp.ExpedicaoPlc;

public interface ExpedicaoPlcMapper {
    public static void updateData(byte[] data, ExpedicaoPlc expedicaoPlc){
        expedicaoPlc.setRecebidoOP((data[0] & 0x01) != 0);

        expedicaoPlc.setRecebidoExpedicao((data[2] & 0x01) != 0);
        expedicaoPlc.setIniciarGuardar((data[2] & 0x02) != 0);

        expedicaoPlc.setPosicaoGuardar(((data[4] & 0xFF) << 8) | (data[5] & 0xFF));

        int[] pedidosExpedicao = new int[12];
        for (int i = 0; i < 12; i++) {
            int byteIndex = 6 + (i * 2);
            pedidosExpedicao[i] = ((data[byteIndex] & 0xFF) << 8) | (data[byteIndex + 1] & 0xFF);
        }
        expedicaoPlc.setPedidosExpedicao(pedidosExpedicao);

        expedicaoPlc.setNumeroOP(((data[30] & 0xFF) << 8) | (data[31] & 0xFF));

        expedicaoPlc.setCancelOP((data[32] & 0x01) != 0);
        expedicaoPlc.setFinishOP((data[32] & 0x02) != 0);
        expedicaoPlc.setStartOP((data[32] & 0x04) != 0);

        expedicaoPlc.setOcupado((data[34] & 0x01) != 0);
        expedicaoPlc.setAguardando((data[34] & 0x02) != 0);
        expedicaoPlc.setManual((data[34] & 0x04) != 0);
        expedicaoPlc.setEmergencia((data[34] & 0x08) != 0);

        expedicaoPlc.setPedirPosicao((data[36] & 0x01) != 0);

        expedicaoPlc.setPosicaoGuardado(((data[38] & 0xFF) << 8) | (data[39] & 0xFF));

        expedicaoPlc.setPosicaoRemovido(((data[40] & 0xFF) << 8) | (data[41] & 0xFF));

        expedicaoPlc.setAdicionar((data[42] & 0x01) != 0);
        expedicaoPlc.setRemover((data[42] & 0x02) != 0);

        expedicaoPlc.setOpGuardado(((data[44] & 0xFF) << 8) | (data[45] & 0xFF));
    }
}
