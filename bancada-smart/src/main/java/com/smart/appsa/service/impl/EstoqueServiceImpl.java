package com.smart.appsa.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.model.enums.TipoPedido;
import com.smart.appsa.repository.EstoqueRepository;
import com.smart.appsa.service.EstoqueService;

@Service
public class EstoqueServiceImpl implements EstoqueService {

    private final PlcConnectionService plcConnectionService;
    private final EstoqueRepository estoqueRepository;

    @Value("${clp.ip}")
    private String clpIp;

    public EstoqueServiceImpl(PlcConnectionService plcConnectionService,EstoqueRepository estoqueRepository) {
        this.plcConnectionService = plcConnectionService;
        this.estoqueRepository = estoqueRepository;
    }

    @Override
    public int consultarQuantidade(int tipoPedido, int posicao) {
        validarPosicao(posicao);

        int db = resolverDb(tipoPedido);
        int endereco = calcularEndereco(posicao);

        PlcConnector connector = obterConector();
        try {
            return connector.readInt(db, endereco);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar estoque no CLP: " + e.getMessage(), e);
        }
    }

    @Override
    public void adicionarLamina(EstoqueDTO dto) {
        if (dto.getQuantidade() <= 0)
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        validarPosicao(dto.getPosicao());

        int db = resolverDb(dto.getTipoPedido());
        int endereco = calcularEndereco(dto.getPosicao());

        PlcConnector connector = obterConector();
        try {
            int atual = connector.readInt(db, endereco);
            connector.writeInt(db, endereco, atual + dto.getQuantidade());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao adicionar lâminas no CLP: " + e.getMessage(), e);
        }
    }

    @Override
    public void removerLamina(EstoqueDTO dto) {
        if (dto.getQuantidade() <= 0)
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        validarPosicao(dto.getPosicao());

        int atual = consultarQuantidade(dto.getTipoPedido(), dto.getPosicao());
        if (dto.getQuantidade() > atual)
            throw new IllegalArgumentException("Quantidade insuficiente no estoque para remover.");

        int db = resolverDb(dto.getTipoPedido());
        int endereco = calcularEndereco(dto.getPosicao());

        PlcConnector connector = obterConector();
        try {
            connector.writeInt(db, endereco, atual - dto.getQuantidade());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover lâminas no CLP: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares
    // -------------------------------------------------------------------------

    
    //Cada TipoPedido corresponde a um DB diferente no CLP:
    //SIMPLES(1) → DB 1 | DUPLO(2) → DB 2 | TRIPLO(3) → DB 3
    
    private int resolverDb(int tipoPedido) {
        for (TipoPedido tp : TipoPedido.values()) {
            if (tp.getValue() == tipoPedido) {
                return tp.getValue();
            }
        }
        throw new IllegalArgumentException("Tipo de pedido inválido: " + tipoPedido);
    }

    
    //Cada posição ocupa 2 bytes (Int S7) dentro do DB.
    //Posição 1 → endereço 0, posição 2 → endereço 2, etc.
    
    private int calcularEndereco(int posicao) {
        return (posicao - 1) * 2;
    }

    
    //Verifica se a posição existe na tabela de estoque do banco de dados.
    
    private void validarPosicao(int posicao) {
        if (!estoqueRepository.existsByPosicao(posicao))
            throw new IllegalArgumentException("Posição " + posicao + " não encontrada no estoque.");
    }

    
    //Obtém o conector do CLP, lançando exceção se não houver conexão.
    
    private PlcConnector obterConector() {
        PlcConnector connector = plcConnectionService.getConnection(clpIp);
        if (connector == null)
            throw new RuntimeException("Sem conexão com o CLP (" + clpIp + ").");
        return connector;
    }
}
