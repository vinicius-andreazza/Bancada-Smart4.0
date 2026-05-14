package com.smart.appsa.service;

import com.smart.appsa.dto.BlocoDTO;
import com.smart.appsa.dto.PedidoRequestDTO;
import com.smart.appsa.dto.PedidoResponseDTO;
import com.smart.appsa.exception.PedidoIsAlreadyConcluidoException;
import com.smart.appsa.mapper.BlocoMapper;
import com.smart.appsa.mapper.PedidoMapper;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;
import com.smart.appsa.repository.ExpedicaoRepository;
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

    private final ExpedicaoService expedicaoService;
    private final PedidoRepository pedidoRepository;
    private final BlocoService blocoService;
    private final ExpedicaoRepository expedicaoRepository;
    // private final ExpedicaoRepository expedicaoRepository;


    public PedidoResponseDTO criar(PedidoRequestDTO dto) {
        validarCorTampa(dto.corTampa());

        Pedido pedido = PedidoMapper.toEntity(dto);

        validarQuantidadeBlocosPorTipo(pedido);

        pedido.setDataCriacao(LocalDateTime.now());

        atribuirPosPedidoNaExpedicao(pedido);

        pedidoRepository.save(pedido);

        // pedido.setExpedicao(resolverExpedicao(dto.idExpedicao()));
        
        atualizarPedidoDosBlocos(pedido);

        List<Bloco> blocosCriados = criarBlocos(pedido.getBlocos());
        pedido.setBlocos(blocosCriados);

        return PedidoMapper.toResponse(pedido);
    }

    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    public PedidoResponseDTO buscarPorId(Long id) {
        return PedidoMapper.toResponse(findById(id));
    }

    public PedidoResponseDTO buscarPorCodigo(String codPedido) {
        return PedidoMapper.toResponse(
                pedidoRepository.findByCodPedido(codPedido)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Pedido não encontrado com código: " + codPedido)));
    }

    public List<PedidoResponseDTO> buscarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    public List<PedidoResponseDTO> buscarPorTipo(TipoPedido tipoPedido) {
        return pedidoRepository.findByTipoPedido(tipoPedido)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    public List<PedidoResponseDTO> buscarPorCorTampa(CorTampa corTampa) {
        return pedidoRepository.findByCorTampa(corTampa)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }
    /*
     * @Transactional(readOnly = true)
     * public List<PedidoResponseDTO> buscarPorExpedicao(Long idExpedicao) {
     * Expedicao expedicao = expedicaoRepository.findById(idExpedicao)
     * .orElseThrow(() -> new EntityNotFoundException(
     * "Expedição não encontrada com id: " + idExpedicao));
     * return pedidoRepository.findByExpedicao(expedicao)
     * .stream()
     * .map(PedidoMapper::toResponse)
     * .toList();
     * }
     */

    public List<PedidoResponseDTO> buscarPorPeriodoCriacao(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoRepository.findByDataCriacaoBetween(inicio, fim)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    public PedidoResponseDTO atualizarParaConcluido(Long id){
        Pedido pedidoExistente = findById(id);
        if(pedidoExistente.getStatus()==StatusPedido.CONCLUIDO){
            throw new PedidoIsAlreadyConcluidoException("Pedido já está concluido");
        }
        pedidoExistente.setStatus(StatusPedido.CONCLUIDO);

        expedicaoService.atribuirPedido(pedidoExistente.getId());

        pedidoRepository.save(pedidoExistente);

        return PedidoMapper.toResponse(pedidoExistente);
    }

    @Transactional
    public PedidoResponseDTO put(Long id, PedidoRequestDTO dto) {
        Pedido pedidoExistente = findById(id);

        pedidoExistente.setCodPedido(dto.codPedido());
        pedidoExistente.setStatus(dto.status());
        pedidoExistente.setTipoPedido(dto.tipoPedido());
        pedidoExistente.setCorTampa(dto.corTampa());
        pedidoExistente.setDataEntrada(dto.dataEntrada());
        /* pedidoExistente.setExpedicao(resolverExpedicao(dto.idExpedicao())); */

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
         * if (dto.idExpedicao() != null) {
         * pedidoExistente.setExpedicao(resolverExpedicao(dto.idExpedicao()));
         * }
         */

        return PedidoMapper.toResponse(pedidoRepository.save(pedidoExistente));
    }

    @Transactional
    public void deletar(Long id) {
        pedidoRepository.delete(findById(id));
    }

    private List<Bloco> criarBlocos(List<Bloco> blocos){
        List<BlocoDTO> blocoDTOs = blocos.stream().map(b -> BlocoMapper.toDto(b)).map(b -> blocoService.create(b)).toList();
        return blocoDTOs.stream().map(b -> BlocoMapper.toEntity(b)).toList();
    }

    private Pedido findById(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com id: " + id));
    }
    /*
     * private Expedicao resolverExpedicao(Long idExpedicao) {
     * if (idExpedicao == null) return null;
     * return expedicaoRepository.findById(idExpedicao)
     * .orElseThrow(() -> new EntityNotFoundException(
     * "Expedição não encontrada com id: " + idExpedicao));
     * }
     */

    public void validarQuantidadeBlocosPorTipo(Pedido pedido) {
        if(pedido.getBlocos().size()<=0){
            throw new IllegalArgumentException("Quantidade invalida de blocos");
        }
        if(pedido.getTipoPedido().getValue()!=pedido.getBlocos().size()){
            throw new IllegalArgumentException("Quantidade invalida de blocos pelo tipo de pedido");
        }
    }

    public void validarEstoqueParaPedido(Pedido pedido) {
       // List<Bloco> blocos = blocoRepository.findByPedido(pedido);
    }

    private void validarCorTampa(CorTampa corTampa) {
        if (corTampa == null) {
            throw new IllegalArgumentException("A cor da tampa é obrigatória e deve ser um valor válido.");
        }
    }

    private void atualizarPedidoDosBlocos(Pedido pedido){
        pedido.getBlocos().forEach(b -> b.setPedido(pedido));
    }

    private void atribuirPosPedidoNaExpedicao(Pedido pedido){
        Expedicao expedicao = expedicaoRepository.findFirstByPedidoIsNull().orElseThrow(() -> new EntityNotFoundException("Expedição cheio"));

        pedido.setExpedicao(expedicao);
    }
}