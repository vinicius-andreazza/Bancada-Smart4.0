package com.smart.appsa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.smart.appsa.dto.EstoqueDTO;
import com.smart.appsa.event.EstoqueAtualizadoEvent;
import com.smart.appsa.mapper.EstoqueMapper;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.repository.EstoqueRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter conectar() {
        SseEmitter emitter = new SseEmitter(0L);

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        enviarSnapshot(null);
        return emitter;
    }

    @EventListener
    @Order(2)
    public void enviarSnapshot(EstoqueAtualizadoEvent event) {
        if(emitters.size() == 0){
            return;
        }

        List<SseEmitter> removidos = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(
                    SseEmitter.event()
                            .name("estoque")
                            .data(findAll())
                );
            } catch (Exception e) {
                removidos.add(emitter);
            }
        }
        
        emitters.removeAll(removidos);
    }

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
    public void releasePosition(Integer position) {
        Estoque estoqueExistente = estoqueRepository.findByPosicao(position)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Posição não existe: " + position));
                        enviarSnapshot(null);
        estoqueExistente.setCor(0);
    }

    @Transactional
    public void addPosition(Integer position, Integer cor){
        Estoque estoqueExistente = estoqueRepository.findByPosicao(position)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Posição não existe: " + position));
                        enviarSnapshot(null);
        estoqueExistente.setCor(cor);
    }

    @Transactional
    public EstoqueDTO put(Integer position, EstoqueDTO estoqueAtualizado) {
        Estoque estoqueExistente = estoqueRepository.findByPosicao(position)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Posição não existe: " + position));

        estoqueExistente.setCor(estoqueAtualizado.cor());

        eventPublisher.publishEvent(new EstoqueAtualizadoEvent(this, position, estoqueAtualizado.cor()));

        return EstoqueMapper.toDto(estoqueRepository.save(estoqueExistente));
    }

    @Transactional
    public List<EstoqueDTO> putAll(List<EstoqueDTO> listaEstoqueAtualizar) {

        List<Estoque> estoques = validateList(listaEstoqueAtualizar);

        updateList(estoques, listaEstoqueAtualizar);
        
        estoques.forEach(e -> eventPublisher.publishEvent(new EstoqueAtualizadoEvent(this, e.getPosicao(), e.getCor())));

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

    private void validateCor(Integer cor) {
        if (cor < 0 || cor > 3) {
            throw new IllegalArgumentException("Cor invalida");
        }
    }

}
