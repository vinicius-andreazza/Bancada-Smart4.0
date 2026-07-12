package com.smart.appsa.mapper;

import com.smart.appsa.dto.BlocoDTO;
import com.smart.appsa.model.Bloco;

public interface BlocoMapper {

    public static BlocoDTO toDto(Bloco bloco) {
        if (bloco == null) {
            return null;
        }
        return BlocoDTO.builder().id(bloco.getId()).andar(bloco.getAndar()).posEstoque(bloco.getPosEstoque())
                .corBloco(bloco.getCorBloco())
                .laminas(bloco.getLaminas() != null
                        ? bloco.getLaminas().stream().map(l -> LaminaMapper.toDto(l)).toList()
                        : null)
                .pedido(bloco.getPedido()).build();
    }

    public static Bloco toEntity(BlocoDTO blocoDTO) {
        if (blocoDTO == null) {
            return null;
        }
        return Bloco.builder().id(blocoDTO.id()).andar(blocoDTO.andar()).posEstoque(blocoDTO.posEstoque())
                .corBloco(blocoDTO.corBloco())
                .laminas(blocoDTO.laminas() != null
                        ? blocoDTO.laminas().stream().map(l -> LaminaMapper.toEntity(l)).toList()
                        : null)
                .pedido(blocoDTO.pedido()).build();
    }

    public static Bloco copy(Bloco bloco) {
        Bloco novo = Bloco.builder()
                .andar(bloco.getAndar())
                .posEstoque(null)
                .corBloco(bloco.getCorBloco())
                .laminas(
                        bloco.getLaminas() == null
                                ? null
                                : bloco.getLaminas()
                                        .stream()
                                        .map(LaminaMapper::copy)
                                        .toList())
                .build();

        if (novo.getLaminas() != null) {
            novo.getLaminas().forEach(l -> l.setBloco(novo));
        }

        return novo;
    }

}
