package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smart.appsa.dto.ExpedicaoDTO;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.repository.ExpedicaoRepository;
import com.smart.appsa.repository.PedidoRepository;

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
        if (dto.getPosicao() == null || dto.getPosicao() <= 0)
            throw new IllegalArgumentException("A posição da expedição é obrigatória e deve ser maior que zero.");

        if (expedicaoRepository.existsByPosicao(dto.getPosicao()))
            throw new IllegalArgumentException("Já existe uma posição de expedição com o número: " + dto.getPosicao());

        Pedido pedido = null;
        if (dto.getPedidoId() != null) {
            pedido = buscarPedido(dto.getPedidoId());
            validarPedidoParaExpedicao(pedido);
        }

        Expedicao expedicao = Expedicao.builder()
                .posicao(dto.getPosicao())
                .pedido(pedido)
                .build();

        return expedicaoRepository.save(expedicao);
    }

    public Expedicao atribuirPedido(Long expedicaoId, Long pedidoId) {
        Expedicao expedicao = buscarPorId(expedicaoId);

        if (expedicao.getPedido() != null)
            throw new IllegalStateException(
                    "A posição " + expedicao.getPosicao() + " já está ocupada pelo pedido: "
                    + expedicao.getPedido().getCodPedido() + ". Libere-a antes de atribuir outro.");

        Pedido pedido = buscarPedido(pedidoId);
        validarPedidoParaExpedicao(pedido);

        expedicao.setPedido(pedido);
        pedido.setExpedicao(expedicao);

        pedidoRepository.save(pedido);
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

    private Pedido buscarPedido(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + pedidoId));
    }

    /**
     * Apenas pedidos em produção podem ser enviados para uma posição de expedição.
     */
    private void validarPedidoParaExpedicao(Pedido pedido) {
        if (pedido.getStatus() != StatusPedido.PRODUCAO)
            throw new IllegalStateException(
                    "O pedido " + pedido.getCodPedido()
                    + " não pode ser expedido pois seu status é: " + pedido.getStatus()
                    + ". Apenas pedidos em PRODUCAO podem ser atribuídos à expedição.");
    }
}
