package com.smart.appsa.service;

import com.smart.appsa.dto.PedidoRequestDTO;
import com.smart.appsa.dto.PedidoResponseDTO;
import com.smart.appsa.mapper.PedidoMapper;

import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    //private final ExpedicaoRepository expedicaoRepository;


    @Transactional
    public PedidoResponseDTO criar(PedidoRequestDTO dto) {
        Pedido pedido = PedidoMapper.toEntity(dto);
        pedido.setDataCriacao(LocalDateTime.now());
        //pedido.setExpedicao(resolverExpedicao(dto.idExpedicao()));
        return PedidoMapper.toResponse(pedidoRepository.save(pedido));
    }



    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Long id) {
        return PedidoMapper.toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorCodigo(String codPedido) {
        return PedidoMapper.toResponse(
                pedidoRepository.findByCodPedido(codPedido)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Pedido não encontrado com código: " + codPedido)));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorTipo(TipoPedido tipoPedido) {
        return pedidoRepository.findByTipoPedido(tipoPedido)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorCorTampa(CorTampa corTampa) {
        return pedidoRepository.findByCorTampa(corTampa)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }
    /*
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorExpedicao(Long idExpedicao) {
        Expedicao expedicao = expedicaoRepository.findById(idExpedicao)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Expedição não encontrada com id: " + idExpedicao));
        return pedidoRepository.findByExpedicao(expedicao)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    } */

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorPeriodoCriacao(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoRepository.findByDataCriacaoBetween(inicio, fim)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }



    @Transactional
    public PedidoResponseDTO put(Long id, PedidoRequestDTO dto) {
        Pedido pedidoExistente = findById(id);

        pedidoExistente.setCodPedido(dto.codPedido());
        pedidoExistente.setStatus(dto.status());
        pedidoExistente.setTipoPedido(dto.tipoPedido());
        pedidoExistente.setCorTampa(dto.corTampa());
        pedidoExistente.setDataEntrada(dto.dataEntrada());
        /*pedidoExistente.setExpedicao(resolverExpedicao(dto.idExpedicao()));*/

        return PedidoMapper.toResponse(pedidoRepository.save(pedidoExistente));
    }



    @Transactional
    public PedidoResponseDTO patch(Long id, PedidoRequestDTO dto) {
        Pedido pedidoExistente = findById(id);

        if (dto.codPedido() != null) {
            if (dto.codPedido().isBlank())
                throw new IllegalArgumentException("O código do pedido não pode ser vazio.");
            pedidoExistente.setCodPedido(dto.codPedido());
        }

        if (dto.status() != null) {
            pedidoExistente.setStatus(dto.status());
        }

        if (dto.tipoPedido() != null) {
            pedidoExistente.setTipoPedido(dto.tipoPedido());
        }

        if (dto.corTampa() != null) {
            pedidoExistente.setCorTampa(dto.corTampa());
        }

        if (dto.dataEntrada() != null) {
            pedidoExistente.setDataEntrada(dto.dataEntrada());
        }
        /* 
        if (dto.idExpedicao() != null) {
            pedidoExistente.setExpedicao(resolverExpedicao(dto.idExpedicao()));
        }*/

        return PedidoMapper.toResponse(pedidoRepository.save(pedidoExistente));
    }



    @Transactional
    public void deletar(Long id) {
        pedidoRepository.delete(findById(id));
    }


    private Pedido findById(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com id: " + id));
    }
    /*
    private Expedicao resolverExpedicao(Long idExpedicao) {
        if (idExpedicao == null) return null;
        return expedicaoRepository.findById(idExpedicao)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Expedição não encontrada com id: " + idExpedicao));
    } */
}