package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smart.appsa.dto.ExpedicaoDTO;
import com.smart.appsa.dto.PedidoRequestDTO;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.repository.ExpedicaoRepository;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpedicaoService {

    private final ExpedicaoRepository expedicaoRepository;
    private final PedidoRepository pedidoRepository;

    public Expedicao buscarPorId(Long id) {
        return expedicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expedição não encontrada com id: " + id));
    }

    public List<Expedicao> listarTodas() {
        return expedicaoRepository.findAll();
    }

    public Expedicao criarPosicao(ExpedicaoDTO dto) {
        if (dto.posicao() == null || dto.posicao() <= 0)
            throw new IllegalArgumentException("A posição da expedição é obrigatória e deve ser maior que zero.");

        if (expedicaoRepository.existsByPosicao(dto.posicao()))
            throw new IllegalArgumentException("Já existe uma posição de expedição com o número: " + dto.posicao());

        Pedido pedido = null;
        if (dto.pedido() != null) {
            pedido = buscarPedido(dto.pedido().id());
        }

        Expedicao expedicao = Expedicao.builder()
                .posicao(dto.posicao())
                .pedido(pedido)
                .build();

        return expedicaoRepository.save(expedicao);
    }

    public Expedicao atribuirPedido(Long pedidoId) {
        Expedicao expedicao = expedicaoRepository.findFirstByPedidoIsNull().orElseThrow(() -> new EntityNotFoundException("Expedição cheio"));

        Pedido pedido = buscarPedido(pedidoId);

        expedicao.setPedido(pedido);

        return expedicaoRepository.save(expedicao);
    }

    public Expedicao liberarPosicao(Long expedicaoId) {
        Expedicao expedicao = buscarPorId(expedicaoId);

        if (expedicao.getPedido() == null)
            throw new IllegalStateException("A posição " + expedicao.getPosicao() + " já está livre.");

        Pedido pedido = expedicao.getPedido();
        pedido.setStatus(StatusPedido.CONCLUIDO);
        pedido.setExpedicao(null);
        pedidoRepository.save(pedido);

        expedicao.setPedido(null);
        return expedicaoRepository.save(expedicao);
    }

    public void removerPosicao(Long id) {
        Expedicao expedicao = buscarPorId(id);

        if (expedicao.getPedido() != null)
            throw new IllegalStateException(
                    "Não é possível remover a posição " + expedicao.getPosicao()
                    + " pois ela possui um pedido atribuído. Libere-a primeiro.");

        expedicaoRepository.delete(expedicao);
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares
    // -------------------------------------------------------------------------

    private Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + id));
    }

}
