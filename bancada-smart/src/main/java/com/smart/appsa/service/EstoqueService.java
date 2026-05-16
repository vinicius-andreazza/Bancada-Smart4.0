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
 
    private static final List<Integer> CORES_VALIDAS = List.of(0, 1, 2, 3);
 
    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------
 
    @Transactional
    public Estoque create(Estoque estoque) {
        validarCor(estoque.getCor());
        validarPosicaoUnica(estoque.getPosicao(), null);
        return estoqueRepository.save(estoque);
    }
 
    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------
 
    public List<Estoque> findAll() {
        return estoqueRepository.findAll();
    }
 
    public Estoque findById(Long id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado com id: " + id));
    }
 
    public Estoque findByPosicao(Integer posicao) {
        return estoqueRepository.findByPosicao(posicao)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nenhuma posição de estoque encontrada com posicao: " + posicao));
    }
 
    public List<EstoqueDTO> findByCor(Integer cor) {
        validarCor(cor);
        return estoqueRepository.findByCor(cor).stream().map(EstoqueMapper::toDto).toList();
    }

    public Estoque findFirstByCor(Integer cor){
        return estoqueRepository.findFirstByCor(cor).orElseThrow(() -> new EntityNotFoundException(
            "Nenhuma posição de estoque encontrada com cor: " + cor));
    }
 
    // -------------------------------------------------------------------------
    // UPDATE (PUT)
    // -------------------------------------------------------------------------
 
    @Transactional
    public Estoque put(Long id, Estoque estoqueAtualizado) {
        Estoque estoqueExistente = findById(id);
 
        validarCor(estoqueAtualizado.getCor());
 
        estoqueExistente.setCor(estoqueAtualizado.getCor());
 
        return estoqueRepository.save(estoqueExistente);
    }
 
    // -------------------------------------------------------------------------
    // PATCH
    // -------------------------------------------------------------------------
 
    @Transactional
    public Estoque patch(Long id, Estoque estoqueParcial) {
        Estoque estoqueExistente = findById(id);
 
        if (estoqueParcial.getCor() != null) {
            validarCor(estoqueParcial.getCor());
            estoqueExistente.setCor(estoqueParcial.getCor());
        }
 
        return estoqueRepository.save(estoqueExistente);
    }
 
    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------
 
    @Transactional
    public void delete(Long id) {
        estoqueRepository.delete(findById(id));
    }
 
    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------
 
    private void validarCor(Integer cor) {
        if (cor == null || !CORES_VALIDAS.contains(cor)) {
            throw new IllegalArgumentException(
                    "Cor inválida: " + cor + ". Valores permitidos: " + CORES_VALIDAS);
        }
    }
 
    private void validarPosicaoUnica(Integer posicao, Long idIgnorar) {
        estoqueRepository.findByPosicao(posicao).ifPresent(existente -> {
            if (!existente.getId().equals(idIgnorar)) {
                throw new IllegalArgumentException(
                        "Já existe uma posição de estoque cadastrada com o número: " + posicao);
            }
        });
    }
}
