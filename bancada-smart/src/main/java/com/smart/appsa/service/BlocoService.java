package com.smart.appsa.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.smart.appsa.dto.BlocoDTO;
import com.smart.appsa.dto.LaminaDTO;
import com.smart.appsa.mapper.BlocoMapper;
import com.smart.appsa.mapper.LaminaMapper;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.repository.BlocoRepository;
import com.smart.appsa.repository.EstoqueRepository;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlocoService {

    private final BlocoRepository blocoRepository;
    private final EstoqueRepository estoqueRepository;
    private final PedidoRepository pedidoRepository;
    private final LaminaService laminaService;

    private final EstoqueService estoqueService;



    public BlocoDTO create(BlocoDTO dto) {
        validarDTO(dto);

        Bloco bloco = BlocoMapper.toEntity(dto);
        
        Pedido pedido = resolverPedido(dto.pedido().getId());

        bloco.setPedido(pedido);

        verificarPosicao(bloco);

        blocoRepository.save(bloco);
        
        atualizarBlocoDasLaminas(bloco);

        criarLaminas(bloco.getLaminas(), bloco);

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
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com id: " + idPedido));
        return blocoRepository.findByPedido(pedido)
                .stream()
                .map(BlocoMapper::toDto)
                .toList();
    }


    @Transactional
    public BlocoDTO put(Long id, BlocoDTO dto) {
        validarDTO(dto);

        Bloco blocoExistente = findEntityById(id);
        Pedido pedido = resolverPedido(dto.pedido().getId());

        blocoExistente.setVl_cor(dto.vl_cor());
        blocoExistente.setPedido(pedido);
        // posEstoque: TO DO — atribuir conforme cor do bloco

        return BlocoMapper.toDto(blocoRepository.save(blocoExistente));
    }



    @Transactional
    public BlocoDTO patch(Long id, BlocoDTO dto) {
        Bloco blocoExistente = findEntityById(id);

        if (dto.vl_cor() != null) {
            blocoExistente.setVl_cor(dto.vl_cor());
        }

        if (dto.pedido() != null && dto.pedido().getId() != null) {
            blocoExistente.setPedido(resolverPedido(dto.pedido().getId()));
        }

        // posEstoque: TO DO — atribuir conforme cor do bloco

        return BlocoMapper.toDto(blocoRepository.save(blocoExistente));
    }



    @Transactional
    public void delete(Long id) {
        blocoRepository.delete(findEntityById(id));
    }


    private List<Lamina> criarLaminas(List<Lamina> laminas, Bloco bloco){
        if(laminas == null){
            return null;
        }
        List<LaminaDTO> blocoDTOs = laminas.stream().map(l -> LaminaMapper.toDto(l)).map(l -> laminaService.criarLamina(l, bloco)).toList();
        return blocoDTOs.stream().map(b -> LaminaMapper.toEntity(b)).toList();
    }


    private Bloco findEntityById(Long id) {
        return blocoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bloco não encontrado com id: " + id));
    }

    private Pedido resolverPedido(Long idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com id: " + idPedido));
    }

    private void validarDTO(BlocoDTO dto) {
        if (dto.vl_cor() == null) {
            throw new IllegalArgumentException("A cor do bloco é obrigatória.");
        }
        if (dto.pedido() == null || dto.pedido().getId() == null) {
            throw new IllegalArgumentException("O pedido do bloco é obrigatório.");
        }
    }

    private void atualizarBlocoDasLaminas(Bloco bloco){
        if(bloco.getLaminas() != null){
            bloco.getLaminas().forEach(l -> l.setBloco(bloco));
        }
    }

    private void verificarPosicao(Bloco bloco){
        Estoque pos = estoqueService.findFirstByCor(bloco.getVl_cor().getValue());
        bloco.setPosEstoque(pos.getPosicao());
        pos.setCor(0);
        estoqueService.put(pos.getId(), pos);
    }
}