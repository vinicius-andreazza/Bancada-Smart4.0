package com.smart.appsa.service;

import com.smart.appsa.dto.BlocoDTO;
import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.CountStatus;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.exception.BlocoQuantityException;
import com.smart.appsa.exception.DuplicatedAndarException;
import com.smart.appsa.exception.PedidoIsAlreadyConcluidoException;
import com.smart.appsa.mapper.BlocoMapper;
import com.smart.appsa.mapper.PedidoMapper;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.AndarBloco;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final ExpedicaoService expedicaoService;
    private final PedidoRepository pedidoRepository;
    private final BlocoService blocoService;
    private final FilaProducao filaProducao;

    @Transactional
    public PedidoResponseDTO create(PedidoRequestDTO dto) {
        Pedido pedido = PedidoMapper.toEntity(dto);

        validatePedido(pedido, dto);

        pedido.setCodPedido(pedidoRepository.findNextAvailableCodPedido());

        pedido.setDataCriacao(LocalDateTime.now());

        pedidoRepository.save(pedido);

        updateBlocoReferencePedido(pedido);

        List<Bloco> blocosCriados = createBlocos(pedido.getBlocos());
        pedido.setBlocos(blocosCriados);

        return PedidoMapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponseDTO remake(Integer codPedido) {
        Pedido pedidoOriginal = pedidoRepository.findByCodPedido(codPedido)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pedido não encontrado com código: " + codPedido));

        Pedido pedidoNovo = PedidoMapper.copy(pedidoOriginal);
        resetPedido(pedidoNovo);

        pedidoNovo.setDataCriacao(LocalDateTime.now());

        pedidoRepository.save(pedidoNovo);

        return PedidoMapper.toResponse(pedidoNovo);
    }

    private void resetPedido(Pedido pedido) {
        pedido.setDataEntrada(null);
        pedido.setDataInicio(null);
        pedido.setPosExpedicao(null);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.getBlocos().forEach(b -> {
            b.setId(null);
            b.setPosEstoque(null);
            b.getLaminas().forEach(l -> l.setId(null));
        });
    }

    public CountStatus countStatus() {
        int total = Integer.parseInt(pedidoRepository.count() + "");
        int pendente = pedidoRepository.countByStatus(StatusPedido.PENDENTE);
        int producao = pedidoRepository.countByStatus(StatusPedido.PRODUCAO);
        int concluido = pedidoRepository.countByStatus(StatusPedido.CONCLUIDO);
        int cancelado = pedidoRepository.countByStatus(StatusPedido.CANCELADO);
        return CountStatus.builder().total(total).pendentes(pendente).producao(producao).concluidos(concluido)
                .cancelado(cancelado).build();
    }

    public Page<PedidoResponseDTO> findAll(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(PedidoMapper::toResponse);
    }

    public Page<PedidoResponseDTO> findPendente(Pageable pageable) {
        return pedidoRepository.findByStatus(StatusPedido.PENDENTE, pageable).map(PedidoMapper::toResponse);
    }

    public Page<PedidoResponseDTO> findProducao(Pageable pageable) {
        return pedidoRepository.findByStatus(StatusPedido.PRODUCAO, pageable).map(PedidoMapper::toResponse);
    }

    public Page<PedidoResponseDTO> findConcluido(Pageable pageable) {
        return pedidoRepository.findByStatus(StatusPedido.CONCLUIDO, pageable).map(PedidoMapper::toResponse);
    }

    public Page<PedidoResponseDTO> findCancelado(Pageable pageable) {
        return pedidoRepository.findByStatus(StatusPedido.CANCELADO, pageable).map(PedidoMapper::toResponse);
    }

    public PedidoResponseDTO findById(Long id) {
        return PedidoMapper.toResponse(
                pedidoRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Pedido não existe")));
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO findByCodigo(Integer codPedido) {
        return PedidoMapper.toResponse(
                pedidoRepository.findByCodPedido(codPedido)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Pedido não encontrado com código: " + codPedido)));
    }

    public Pedido findPedidoByCodigo(Integer codPedido) {
        return pedidoRepository.findByCodPedido(codPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe"));
    }

    public List<PedidoResponseDTO> findByStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    public List<PedidoResponseDTO> findByTipo(TipoPedido tipoPedido) {
        return pedidoRepository.findByTipoPedido(tipoPedido)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    public List<PedidoResponseDTO> findByCreationPeriod(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoRepository.findByDataCriacaoBetween(inicio, fim)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    public PedidoResponseDTO findLatestConcluido() {
        return PedidoMapper.toResponse(pedidoRepository.findFirstByStatusOrderByDataEntradaDesc(StatusPedido.CONCLUIDO)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe")));
    }

    public boolean existsByCodigo(int codPedido) {
        return pedidoRepository.existsByCodPedido(codPedido);
    }

    @Transactional
    public PedidoResponseDTO updateToConcluido(Long id) {
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe"));

        if (pedidoExistente.getStatus() == StatusPedido.CONCLUIDO) {
            throw new PedidoIsAlreadyConcluidoException("Pedido já está concluido");
        }
        pedidoExistente.setStatus(StatusPedido.CONCLUIDO);
        pedidoExistente.setDataEntrada(LocalDateTime.now());

        assignPosPedidoInExpedicao(pedidoExistente);

        expedicaoService.assignPedido(pedidoExistente.getId());

        pedidoRepository.save(pedidoExistente);

        return PedidoMapper.toResponse(pedidoExistente);
    }

    public PedidoResponseDTO removeDaFila(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe"));
        if (!filaProducao.remover(pedido.getCodPedido())) {
            throw new IllegalStateException("Pedido não está na fila");
        }
        return PedidoMapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponseDTO put(Long id, PedidoRequestDTO dto) {
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe"));
        validatePedido(PedidoMapper.toEntity(dto), dto);

        pedidoExistente.setCodPedido(dto.codPedido());
        pedidoExistente.setStatus(dto.status());
        pedidoExistente.setTipoPedido(dto.tipoPedido());
        pedidoExistente.setCorTampa(dto.corTampa());
        pedidoExistente.setDataEntrada(dto.dataEntrada());
        dto.blocos().forEach(b -> blocoService.put(b));

        return PedidoMapper.toResponse(pedidoRepository.save(pedidoExistente));
    }

    @Transactional
    public PedidoResponseDTO patch(Long id, PedidoRequestDTO dto) {
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe"));

        validateUpdate(dto);
        System.out.println(dto);
        if (dto.codPedido() != null) {
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
        if (dto.blocos() != null && !dto.blocos().isEmpty()) {
            dto.blocos().forEach(b -> {
                if (b.id() == null) {
                    blocoService.create(b);
                } else {
                    blocoService.patch(b);
                }
            });
        }

        return PedidoMapper.toResponse(pedidoRepository.save(pedidoExistente));
    }

    @Transactional
    public void delete(Long id) {
        pedidoRepository.delete(
                pedidoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pedido não existe")));
    }

    private List<Bloco> createBlocos(List<Bloco> blocos) {
        List<BlocoDTO> blocoDTOs = blocos.stream().map(b -> BlocoMapper.toDto(b)).map(b -> blocoService.create(b))
                .toList();
        return blocoDTOs.stream().map(b -> BlocoMapper.toEntity(b)).toList();
    }

    public void validateBlocosQuantityByType(Pedido pedido) {
        if (pedido.getBlocos().size() <= 0) {
            throw new BlocoQuantityException("Quantidade invalida de blocos");
        }
        if (pedido.getTipoPedido().getValue() != pedido.getBlocos().size()) {
            throw new BlocoQuantityException("Quantidade invalida de blocos pelo tipo de pedido");
        }
    }

    private void validateCorTampa(CorTampa corTampa) {
        if (corTampa == null) {
            throw new IllegalArgumentException("A cor da tampa é obrigatória e deve ser um valor válido.");
        }
    }

    private void validateDuplicatedFloors(List<Bloco> blocos) {
        Map<AndarBloco, Long> contagemPorAndar = blocos.stream()
                .collect(Collectors.groupingBy(
                        Bloco::getAndar,
                        Collectors.counting()));

        List<AndarBloco> andaresDuplicados = contagemPorAndar.entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (!andaresDuplicados.isEmpty()) {
            throw new DuplicatedAndarException(
                    "Existem blocos com andares duplicados: " + andaresDuplicados);
        }
    }

    private void updateBlocoReferencePedido(Pedido pedido) {
        pedido.getBlocos().forEach(b -> b.setPedido(pedido));
    }

    public void assignPosPedidoInExpedicao(Pedido pedido) {
        Expedicao expedicao = expedicaoService.findFirstAvailable();

        pedido.setPosExpedicao(expedicao.getPosicao());
    }

    private void validatePedido(Pedido pedido, PedidoRequestDTO dto) {
        validateCorTampa(dto.corTampa());
        validateBlocosQuantityByType(pedido);
        validateDuplicatedFloors(pedido.getBlocos());
    }

    private void validateUpdate(PedidoRequestDTO dto) {
        if (dto.blocos() == null) {
            return;
        }
        if (dto.tipoPedido().getValue() != dto.blocos().size()) {
            throw new BlocoQuantityException("Quantidade invalida de blocos pelo tipo de pedido");
        }
    }
}