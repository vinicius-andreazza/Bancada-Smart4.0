package com.smart.appsa.mapper.clp;

import java.nio.ByteBuffer;
import java.util.List;

import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.model.Pedido;

public interface ProducaoPlcMapper {
    public static byte[] mapToBytes(Pedido pedido) {
        ByteBuffer buffer = ByteBuffer.allocate(60);

        List<Bloco> blocos = pedido.getBlocos();

        for (int i = 0; i < 3; i++) {
            if (i < blocos.size()) {
                writeBloco(blocos.get(i), buffer);
            } else {
                writeEmptyBloco(buffer);
            }
        }

        buffer.putShort((short) pedido.getCodPedido().intValue());
        buffer.putShort((short) pedido.getTipoPedido().getValue());
        buffer.putShort((short) pedido.getPosExpedicao().intValue());

        return buffer.array();
    }
    

    private static void writeBloco(Bloco bloco, ByteBuffer buffer) {
        buffer.putShort((short) bloco.getCorBloco().getValue());
        buffer.putShort((short) bloco.getPosEstoque().intValue());

        List<Lamina> laminas = bloco.getLaminas();

        Lamina[] slots = new Lamina[3];
        for (Lamina lamina : laminas) {
            int pos = lamina.getPosicaoLamina().getValue();
            slots[pos - 1] = lamina;
        }

        for (Lamina slot : slots) {
            buffer.putShort(slot != null ? (short) slot.getCorLamina().getValue() : (short) 0);
        }

        for (Lamina slot : slots) {
            buffer.putShort(slot != null ? (short) slot.getPadraoLamina().getValue() : (short) 0);
        }

        buffer.putShort((short) 0);
    }

    private static void writeEmptyBloco(ByteBuffer buffer) {
        for (int i = 0; i < 9; i++) {
            buffer.putShort((short) 0);
        }
    }
}
