package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smart.appsa.dto.BlocoDTO;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.repository.BlocoRepository;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlocoService {

    private final BlocoRepository blocoRepository;
    private final PedidoRepository pedidoRepository;



    public BlocoDTO create(BlocoDTO dto) {
        validarDTO(dto);

        Pedido pedido = resolverPedido(dto.pedido().getId());

        return toDTO(blocoRepository.save(bloco));
    }



    public List<BlocoDTO> findAll() {
        return blocoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public BlocoDTO findById(Long id) {
        return toDTO(findEntityById(id));
    }

    public List<BlocoDTO> findByPedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com id: " + idPedido));
        return blocoRepository.findByPedido(pedido)
                .stream()
                .map(this::toDTO)
                .toList();
    }



    @Transactional
    public BlocoDTO put(Long id, BlocoDTO dto) {
        validarDTO(dto);

        Bloco blocoExistente = findEntityById(id);
        Pedido pedido = resolverPedido(dto.pedido().getId());

        blocoExistente.setVl_cor(dto.vl_cor());
        blocoExistente.setPedido(pedido);
        // posEstoque: TO DO — atribuir conforme cor do bloco

        return toDTO(blocoRepository.save(blocoExistente));
    }



    @Transactional
    public BlocoDTO patch(Long id, BlocoDTO dto) {
        Bloco blocoExistente = findEntityById(id);

        if (dto.vl_cor() != null) {
            blocoExistente.setVl_cor(dto.vl_cor());
        }

        if (dto.pedido() != null && dto.pedido().getId() != null) {
            blocoExistente.setPedido(resolverPedido(dto.pedido().getId()));
        }

        // posEstoque: TO DO — atribuir conforme cor do bloco

        return toDTO(blocoRepository.save(blocoExistente));
    }



    @Transactional
    public void delete(Long id) {
        blocoRepository.delete(findEntityById(id));
    }



    private Bloco findEntityById(Long id) {
        return blocoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bloco não encontrado com id: " + id));
    }

    private Pedido resolverPedido(Long idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com id: " + idPedido));
    }

    private void validarDTO(BlocoDTO dto) {
        if (dto.vl_cor() == null) {
            throw new IllegalArgumentException("A cor do bloco é obrigatória.");
        }
        if (dto.pedido() == null || dto.pedido().getId() == null) {
            throw new IllegalArgumentException("O pedido do bloco é obrigatório.");
        }
    }

    private BlocoDTO toDTO(Bloco bloco) {
        return new BlocoDTO(
                bloco.getId(),
                bloco.getVl_cor(),
                bloco.getPosEstoque(),
                bloco.getPedido(),
                bloco.getLaminas()
        );
    }
}