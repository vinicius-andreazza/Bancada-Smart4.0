package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smart.appsa.dto.ExpedicaoDTO;
import com.smart.appsa.mapper.ExpedicaoMapper;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.repository.ExpedicaoRepository;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpedicaoService {

    private final ExpedicaoRepository expedicaoRepository;
    private final PedidoRepository pedidoRepository;

    public Expedicao findById(Long id) {
        return expedicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expedição não encontrada com id: " + id));
    }

    public ExpedicaoDTO findByPosicao(Integer posicao) {
        return ExpedicaoMapper.toDto(expedicaoRepository.findByPosicao(posicao)
                .orElseThrow(() -> new RuntimeException("Expedição não encontrada")));
    }

    public List<ExpedicaoDTO> findAll() {
        return expedicaoRepository.findAll().stream().map(ExpedicaoMapper::toDto).toList();
    }

    public Expedicao findFirstAvailable (){
        return expedicaoRepository.findFirstByPedidoIsNull().orElseThrow(() -> new EntityNotFoundException("Expedição cheio"));
    }

    
    public Expedicao assignPedido(Long pedidoId) {
        Expedicao expedicao = expedicaoRepository.findFirstByPedidoIsNull().orElseThrow(() -> new EntityNotFoundException("Expedição cheio"));

        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new RuntimeException("Pedido não encontrado "));
        
        expedicao.setPedido(pedido);

        return expedicao;
    }

    public Expedicao assignPedidoByPosition(Long pedidoId, int position) {
        Expedicao expedicao = expedicaoRepository.findByPosicao(position).orElseThrow(() -> new IllegalArgumentException("Posição não existe"));

        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new RuntimeException("Pedido não encontrado "));
        
        expedicao.setPedido(pedido);

        return expedicao;
    }

    public Expedicao releasePosicao(Long expedicaoId) {
        Expedicao expedicao = expedicaoRepository.findById(expedicaoId)
                .orElseThrow(() -> new RuntimeException("Expedição não encontrada com id: " + expedicaoId));

        if (expedicao.getPedido() == null)
            throw new IllegalStateException("A posição " + expedicao.getPosicao() + " já está livre.");

        releasePedido(expedicao);

        expedicao.setPedido(null);
        return expedicaoRepository.save(expedicao);
    }

    private void releasePedido(Expedicao expedicao){
        Pedido pedido = pedidoRepository.findById(expedicao.getPedido().getId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado "));
        pedido.setPosExpedicao(null);
        pedidoRepository.save(pedido);
    }

}
