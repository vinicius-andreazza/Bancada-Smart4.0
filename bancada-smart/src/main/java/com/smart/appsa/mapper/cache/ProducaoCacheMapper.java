package com.smart.appsa.mapper.cache;

import java.util.ArrayList;
import java.util.List;

import com.smart.appsa.dto.producao.BlocoSnapshot;
import com.smart.appsa.dto.producao.LaminaSnapshot;
import com.smart.appsa.dto.producao.ProducaoSnapshot;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.model.Pedido;

public interface ProducaoCacheMapper {
    public static ProducaoSnapshot constructSnapshot(Pedido pedido) {
        List<Bloco> blocos = pedido.getBlocos();

        List<BlocoSnapshot> blocosSnapshot = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            blocosSnapshot.add(i < blocos.size() ? constructBloco(blocos.get(i)) : constructEmptyBloco());
        }

        return ProducaoSnapshot.builder()
                .codPedido(pedido.getCodPedido())
                .tipoPedido(pedido.getTipoPedido().getValue())
                .corTampa(pedido.getCorTampa().getValue())
                .posExpedicao(pedido.getPosExpedicao())
                .blocos(blocosSnapshot)
                .build();
    }
    private static BlocoSnapshot constructBloco(Bloco bloco) {
        Lamina[] slots = new Lamina[3];
        for (Lamina lamina : bloco.getLaminas()) {
            int pos = lamina.getPosicaoLamina().getValue();
            slots[pos - 1] = lamina;
        }

        List<LaminaSnapshot> laminas = new ArrayList<>();
        for (Lamina slot : slots) {
            laminas.add(LaminaSnapshot.builder()
                    .corLamina(slot != null ? slot.getCorLamina().getValue() : 0)
                    .padraoLamina(slot != null ? slot.getPadraoLamina().getValue() : 0)
                    .build());
        }

        return BlocoSnapshot.builder()
                .corBloco(bloco.getCorBloco().getValue())
                .posEstoque(bloco.getPosEstoque() != null ? bloco.getPosEstoque() : 0)
                .laminas(laminas)
                .build();
    }

    private static BlocoSnapshot constructEmptyBloco() {
        List<LaminaSnapshot> laminas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            laminas.add(LaminaSnapshot.builder()
                    .corLamina(0)
                    .padraoLamina(0)
                    .build());
        }

        return BlocoSnapshot.builder()
                .corBloco(0)
                .posEstoque(0)
                .laminas(laminas)
                .build();
    }
}
