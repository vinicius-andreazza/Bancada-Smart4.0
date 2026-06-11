package com.smart.appsa.mapper.clp;

import com.smart.appsa.model.plc.EstoquePlc;

public interface EstoquePlcMapper {
    public static void updateData(byte[] data, EstoquePlc estoquePlc){
        estoquePlc.setRecebidoOP((data[0] & 0x01) != 0);

        estoquePlc.setIniciarPedido((data[62] & (byte) 0x01) != 0);
        estoquePlc.setRecebidoEstoque((data[64] & 0x01) != 0);
        estoquePlc.setIniciarGuardar((data[64] & 0x02) != 0);

        estoquePlc.setPosicaoGuardar(((data[66] & 0xFF) << 8) | (data[67] & 0xFF));

        byte[] posicoes = new byte[28];
        for (int c = 0; c < 28; c++) {
            posicoes[c] = data[68 + c];
        }

        estoquePlc.setPosicoes(posicoes);

        estoquePlc.setNumeroPedido(((data[96] & 0xFF) << 8) | (data[97] & 0xFF));
        estoquePlc.setCancelOp((data[98] & 0x01) != 0);
        estoquePlc.setFinishOp((data[98] & 0x02) != 0);
        estoquePlc.setStartOp((data[98] & 0x04) != 0);

        estoquePlc.setOcupado((data[100] & 0x01) != 0);
        estoquePlc.setAguardando((data[100] & 0x02) != 0);
        estoquePlc.setManual((data[100] & 0x04) != 0);
        estoquePlc.setEmergencia((data[100] & 0x08) != 0);

        estoquePlc.setPedirPosicao((data[102] & 0x01) != 0);
        estoquePlc.setPosicaoEstoque(((data[104] & 0xFF) << 8) | (data[105] & 0xFF));
        estoquePlc.setAdicionarEstoque((data[106] & 0x01) != 0);

        estoquePlc.setRemoverEstoque((boolean) ((data[106] & 0x02) != 0));

        estoquePlc.setRetornoEstoqueCheio((data[106] & 0x04) != 0);
        estoquePlc.setCorGuardarEstoque(((data[108] & 0xFF) << 8) | (data[109] & 0xFF));

        estoquePlc.setRemoverEstoque((data[106] & 0x02) != 0);
    }
}