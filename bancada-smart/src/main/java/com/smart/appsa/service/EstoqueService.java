package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.mapper.EstoqueMapper;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.repository.EstoqueRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public List<Estoque> findAll() {
        return estoqueRepository.findAll();
    }

    public Estoque findByPosicao(Integer posicao) {
        return estoqueRepository.findByPosicao(posicao)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Posição não existe: " + posicao));
    }

    public List<EstoqueDTO> findByCor(Integer cor) {
        if (cor < 1 || cor > 3) {
            throw new IllegalArgumentException("Cor invalida");
        }
        return estoqueRepository.findByCor(cor).stream().map(EstoqueMapper::toDto).toList();
    }

    public Estoque findFirstByCor(Integer cor) {
        return estoqueRepository.findFirstByCor(cor).orElseThrow(() -> new EntityNotFoundException(
                "Nenhuma posição de estoque encontrada com cor: " + cor));
    }

    @Transactional
    public Estoque put(Long position, Estoque estoqueAtualizado) {
        Estoque estoqueExistente = findByPosicao(Math.toIntExact(position));

        estoqueExistente.setCor(estoqueAtualizado.getCor());

        return estoqueRepository.save(estoqueExistente);
    }

    @Transactional
    public List<Estoque> putAll(List<EstoqueDTO> listaEstoqueAtualizar) {

        List<Estoque> estoques = validarLista(listaEstoqueAtualizar);

        updateList(estoques, listaEstoqueAtualizar);

        return estoqueRepository.saveAll(estoques);
    }

    private List<Estoque> validarLista(List<EstoqueDTO> listaEstoqueAtualizar) {
        return listaEstoqueAtualizar.stream().distinct().map(e -> findByPosicao(e.posicao())).toList();
    }

    private void updateList(
            List<Estoque> listaEstoque,
            List<EstoqueDTO> listaEstoqueAtualizar) {

        listaEstoque.sort((e1, e2) -> Integer.compare(e1.getPosicao(), e2.getPosicao()));

        listaEstoqueAtualizar.sort((e1, e2) -> Integer.compare(e1.posicao(), e2.posicao()));

        for (int i = 0; i < listaEstoque.size(); i++) {
            listaEstoque.get(i)
                    .setCor(listaEstoqueAtualizar.get(i).cor());
        }
    }

}
