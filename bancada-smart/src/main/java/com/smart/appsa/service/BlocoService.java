package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.appsa.dto.BlocoDTO;
import com.smart.appsa.dto.LaminaDTO;
import com.smart.appsa.mapper.BlocoMapper;
import com.smart.appsa.mapper.EstoqueMapper;
import com.smart.appsa.mapper.LaminaMapper;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.repository.BlocoRepository;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlocoService {

    private final BlocoRepository blocoRepository;
    private final PedidoRepository pedidoRepository;
    private final LaminaService laminaService;

    private final EstoqueService estoqueService;

    @Transactional
    public BlocoDTO create(BlocoDTO dto) {
        validateDTO(dto);

        Bloco bloco = BlocoMapper.toEntity(dto);

        Pedido pedido = findPedidoById(dto.pedido().getId());

        bloco.setPedido(pedido);

        //assignEstoquePosition(bloco);
        bloco.setPosEstoque(null);

        blocoRepository.save(bloco);

        updateLaminasInBloco(bloco);

        createLaminas(bloco.getLaminas(), bloco);

        return BlocoMapper.toDto(bloco);
    }

    public List<BlocoDTO> findAll() {
        return blocoRepository.findAll()
                .stream()
                .map(BlocoMapper::toDto)
                .toList();
    }

    public BlocoDTO findById(Long id) {
        return BlocoMapper.toDto(findEntityById(id));
    }

    public List<BlocoDTO> findByPedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não existe"));

        return blocoRepository.findByPedido(pedido)
                .stream()
                .map(BlocoMapper::toDto)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        blocoRepository.delete(findEntityById(id));
    }

    private List<Lamina> createLaminas(List<Lamina> laminas, Bloco bloco) {
        if (laminas == null || laminas.isEmpty()) {
            return List.of();
        }

        List<LaminaDTO> laminasDTO = laminas.stream().map(LaminaMapper::toDto).map(l -> laminaService.create(l, bloco))
                .toList();

        return laminasDTO.stream().map(b -> LaminaMapper.toEntity(b)).toList();
    }

    private Bloco findEntityById(Long id) {
        return blocoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bloco não encontrado com id: " + id));
    }

    private Pedido findPedidoById(Long idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com id: " + idPedido));
    }

    private void validateDTO(BlocoDTO dto) {
        if (dto.corBloco() == null) {
            throw new IllegalArgumentException("A cor do bloco é obrigatória.");
        }
        if (dto.pedido() == null || dto.pedido().getId() == null) {
            throw new IllegalArgumentException("O pedido do bloco é obrigatório.");
        }
        if (dto.laminas().size() > 3) {
            throw new IllegalArgumentException("Número inválido de laminas");
        }
    }

    private void updateLaminasInBloco(Bloco bloco) {
        if (bloco.getLaminas() != null) {
            bloco.getLaminas().forEach(l -> l.setBloco(bloco));
        }
    }
    @Transactional
    public void assignEstoquePosition(Bloco bloco) {
        Estoque pos = estoqueService.findFirstByCor(bloco.getCorBloco().getValue());
        bloco.setPosEstoque(pos.getPosicao());
        pos.setCor(0);
        estoqueService.put(pos.getPosicao(), EstoqueMapper.toDto(pos));
    }
}