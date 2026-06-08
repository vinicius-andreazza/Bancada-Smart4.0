package com.smart.appsa.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.mapper.EstoqueMapper;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.repository.EstoqueRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public List<EstoqueDTO> findAll() {
        return estoqueRepository.findAll().stream().map(EstoqueMapper::toDto).toList();
    }

    public EstoqueDTO findByPosicao(Integer posicao) {
        return EstoqueMapper.toDto(estoqueRepository.findByPosicao(posicao)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Posição não existe: " + posicao)));
    }

    public List<EstoqueDTO> findByCor(Integer cor) {
        validateCor(cor);
        return estoqueRepository.findByCor(cor).stream().map(EstoqueMapper::toDto).toList();
    }

    public Estoque findFirstByCor(Integer cor) {
        validateCor(cor);
        return estoqueRepository.findFirstByCor(cor).orElseThrow(() -> new EntityNotFoundException(
                "Nenhuma posição de estoque encontrada com cor: " + cor));
    }

    @Transactional
    public EstoqueDTO put(Integer position, EstoqueDTO estoqueAtualizado) {
        Estoque estoqueExistente = EstoqueMapper.toEntity(findByPosicao(position));

        estoqueExistente.setCor(estoqueAtualizado.cor());

        return EstoqueMapper.toDto(estoqueRepository.save(estoqueExistente));
    }

    @Transactional
    public List<EstoqueDTO> putAll(List<EstoqueDTO> listaEstoqueAtualizar) {

        List<Estoque> estoques = validateList(listaEstoqueAtualizar);

        updateList(estoques, listaEstoqueAtualizar);

        return estoqueRepository.saveAll(estoques).stream().map(EstoqueMapper::toDto).toList();
    }

    private List<Estoque> validateList(List<EstoqueDTO> listaEstoqueAtualizar) {
        return listaEstoqueAtualizar.stream().distinct().map(e -> EstoqueMapper.toEntity(findByPosicao(e.posicao())))
                .toList();
    }

    private void updateList(
            List<Estoque> listaEstoque,
            List<EstoqueDTO> listaEstoqueAtualizar) {

        Map<Integer, EstoqueDTO> dtoByPosicao = listaEstoqueAtualizar.stream()
                .collect(Collectors.toMap(EstoqueDTO::posicao, e -> e));

        listaEstoque.forEach(e -> {
            EstoqueDTO dto = dtoByPosicao.get(e.getPosicao());
            if (dto != null)
                e.setCor(dto.cor());
        });
    }

    private void validateCor(Integer cor){
        if (cor < 1 || cor > 3) {
            throw new IllegalArgumentException("Cor invalida");
        }
    }

}
