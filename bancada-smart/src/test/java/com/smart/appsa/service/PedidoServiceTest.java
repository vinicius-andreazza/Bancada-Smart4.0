package com.smart.appsa.service;

import com.smart.appsa.dto.BlocoDTO;
import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.exception.PedidoIsAlreadyConcluidoException;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.*;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private BlocoService blocoService;

    @Mock
    private ExpedicaoService expedicaoService;

    @InjectMocks
    private PedidoService pedidoService;


    @Test
    @Disabled
    void shouldReturnAllPedidosWhenPedidosExist() {
        Pedido p = createPedido();
        when(pedidoRepository.findAll()).thenReturn(List.of(p));

        //List<PedidoResponseDTO> resultado = pedidoService.findAll();

        //assertThat(resultado).hasSize(1);
        //assertThat(resultado.get(0).codPedido()).isEqualTo(1);
    }

    @Test
    void shouldThrowEntityNotFoundWhenFindByIdGivenUnknownId() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Pedido não existe");
    }

    @Test
    void shouldReturnPedidoWhenFindByIdGivenExistingId() {
        Pedido p = createPedido();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));

        PedidoResponseDTO dto = pedidoService.findById(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.codPedido()).isEqualTo(1);
    }

    @Test
    void shouldThrowEntityNotFoundWhenFindByCodigoGivenUnknownCodigo() {
        when(pedidoRepository.findByCodPedido(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.findByCodigo(0))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldReturnPedidoWhenFindByCodigoGivenExistingCodigo() {
        Pedido p = new Pedido();
        p.setId(2L);
        p.setCodPedido(1);
        p.setStatus(StatusPedido.PRODUCAO);
        p.setTipoPedido(TipoPedido.DUPLO);
        p.setBlocos(List.of());
        when(pedidoRepository.findByCodPedido(1)).thenReturn(Optional.of(p));

        PedidoResponseDTO dto = pedidoService.findByCodigo(1);

        assertThat(dto.codPedido()).isEqualTo(1);
    }


    @Test
    void shouldReturnOnlyPendentesWhenFindByStatusGivenPendente() {
        Pedido p = new Pedido();
        p.setId(1L);
        p.setStatus(StatusPedido.PENDENTE);
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of());
        when(pedidoRepository.findByStatus(StatusPedido.PENDENTE)).thenReturn(List.of(p));

        List<PedidoResponseDTO> resultado = pedidoService.findByStatus(StatusPedido.PENDENTE);

        assertThat(resultado).allMatch(r -> r.status() == StatusPedido.PENDENTE);
    }



    @Test
    void shouldThrowEntityNotFoundWhenUpdateToConcluidoGivenUnknownId() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.updateToConcluido(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldThrowPedidoAlreadyConcluidoWhenUpdateToConcluidoGivenConcluido() {
        Pedido p = new Pedido();
        p.setId(1L);
        p.setStatus(StatusPedido.CONCLUIDO);
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of());
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> pedidoService.updateToConcluido(1L))
                .isInstanceOf(PedidoIsAlreadyConcluidoException.class)
                .hasMessageContaining("concluido");
    }

    @Test
    void shouldSetStatusConcluidoWhenUpdateToConcluidoGivenPendente() {
        Pedido p = createPedido();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoResponseDTO resultado = pedidoService.updateToConcluido(1L);

        assertThat(resultado.status()).isEqualTo(StatusPedido.CONCLUIDO);
        assertThat(resultado.dataEntrada()).isNotNull();
    }

    @Test
    void shouldSetDataEntradaNowWhenUpdateToConcluido() {
        Pedido p = createPedido();
        p.setStatus(StatusPedido.PRODUCAO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));
        when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        PedidoResponseDTO resultado = pedidoService.updateToConcluido(1L);
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertThat(resultado.dataEntrada()).isBetween(antes, depois);
    }


    @Test
    void shouldThrowWhenValidateBlocosQuantityGivenEmptyList() {
        Pedido p = new Pedido();
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of());

        assertThatThrownBy(() -> pedidoService.validateBlocosQuantityByType(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade invalida de blocos");
    }

    @Test
    void shouldThrowWhenValidateBlocosQuantityGivenCountMismatchForSimples() {
        Pedido p = new Pedido();
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of(blocoSimples(AndarBloco.PRIMEIRO), blocoSimples(AndarBloco.SEGUNDO)));

        assertThatThrownBy(() -> pedidoService.validateBlocosQuantityByType(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tipo de pedido");
    }

    @Test
    void shouldThrowWhenValidateBlocosQuantityGivenCountMismatchForDuplo() {
        Pedido p = new Pedido();
        p.setTipoPedido(TipoPedido.DUPLO);
        p.setBlocos(List.of(blocoSimples(AndarBloco.PRIMEIRO)));

        assertThatThrownBy(() -> pedidoService.validateBlocosQuantityByType(p))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotThrowWhenValidateBlocosQuantityGivenThreeBlocosForTriplo() {
        Pedido p = new Pedido();
        p.setTipoPedido(TipoPedido.TRIPLO);
        p.setBlocos(List.of(
                blocoSimples(AndarBloco.PRIMEIRO),
                blocoSimples(AndarBloco.SEGUNDO),
                blocoSimples(AndarBloco.TERCEIRO)));

        assertThatNoException().isThrownBy(() -> pedidoService.validateBlocosQuantityByType(p));
    }

    @Test
    void shouldNotThrowWhenValidateBlocosQuantityGivenOneBlocoForSimples() {
        Pedido p = new Pedido();
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of(blocoSimples(AndarBloco.PRIMEIRO)));

        assertThatNoException().isThrownBy(() -> pedidoService.validateBlocosQuantityByType(p));
    }

    @Test
    void shouldNotThrowWhenValidateBlocosQuantityGivenTwoBlocosForDuplo() {
        Pedido p = new Pedido();
        p.setTipoPedido(TipoPedido.DUPLO);
        p.setBlocos(List.of(blocoSimples(AndarBloco.PRIMEIRO), blocoSimples(AndarBloco.SEGUNDO)));

        assertThatNoException().isThrownBy(() -> pedidoService.validateBlocosQuantityByType(p));
    }


    @Test
    void shouldAssignExpedicaoAndCreateBlocosWhenCreateGivenValidPedido() {
        BlocoDTO blocoDto = new BlocoDTO(null, CorBloco.PRETO, null, AndarBloco.PRIMEIRO, null, List.of());
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .codPedido(1)
                .status(StatusPedido.PENDENTE)
                .tipoPedido(TipoPedido.SIMPLES)
                .corTampa(CorTampa.PRETO)
                .blocos(List.of(blocoDto))
                .build();

        Expedicao expedicao = new Expedicao();
        expedicao.setPosicao(5);
        when(expedicaoService.assignPedido(any())).thenReturn(expedicao);
        when(blocoService.create(any())).thenReturn(blocoDto);

        PedidoResponseDTO resultado = pedidoService.create(dto);

        assertThat(resultado.codPedido()).isEqualTo(1);
        assertThat(resultado.dataCriacao()).isNotNull();
        assertThat(resultado.idExpedicao()).isEqualTo(5);
        assertThat(resultado.blocos()).hasSize(1);
        verify(pedidoRepository).save(any(Pedido.class));
        verify(blocoService).create(any(BlocoDTO.class));
    }

    @Test
    void shouldThrowWhenCreateGivenDuplicatedAndares() {
        BlocoDTO b1 = new BlocoDTO(null, CorBloco.PRETO, null, AndarBloco.PRIMEIRO, null, List.of());
        BlocoDTO b2 = new BlocoDTO(null, CorBloco.AZUL, null, AndarBloco.PRIMEIRO, null, List.of());
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .codPedido(200)
                .status(StatusPedido.PENDENTE)
                .tipoPedido(TipoPedido.DUPLO)
                .corTampa(CorTampa.AZUL)
                .blocos(List.of(b1, b2))
                .build();

        assertThatThrownBy(() -> pedidoService.create(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void shouldThrowEntityNotFoundWhenPatchGivenUnknownId() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder().build();
        when(pedidoRepository.findById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.patch(50L, dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldUpdateOnlyNonNullFieldsWhenPatch() {
        Pedido p = createPedido();
        
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));
        when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .codPedido(2)
                .build();

        PedidoResponseDTO resultado = pedidoService.patch(1L, dto);

        assertThat(resultado.codPedido()).isEqualTo(2);
        assertThat(resultado.status()).isEqualTo(StatusPedido.PENDENTE);
    }

    @Test
    void shouldThrowEntityNotFoundWhenDeleteGivenUnknownId() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldRemovePedidoWhenDeleteGivenExistingId() {
        Pedido p = new Pedido();
        p.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));

        assertThatNoException().isThrownBy(() -> pedidoService.delete(1L));
        verify(pedidoRepository).delete(p);
    }

    private Bloco blocoSimples(AndarBloco andar) {
        Bloco b = new Bloco();
        b.setAndar(andar);
        b.setCorBloco(CorBloco.PRETO);
        b.setLaminas(List.of());
        return b;
    }

    private Pedido createPedido(){
        Pedido p = new Pedido();
        p.setId(1L);
        p.setCodPedido(1);
        p.setStatus(StatusPedido.PENDENTE);
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of());
        return p;
    }

}
