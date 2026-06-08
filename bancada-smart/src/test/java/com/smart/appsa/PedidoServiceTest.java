package com.smart.appsa;

import com.smart.appsa.dto.BlocoDTO;
import com.smart.appsa.dto.PedidoRequestDTO;
import com.smart.appsa.dto.PedidoResponseDTO;
import com.smart.appsa.exception.PedidoIsAlreadyConcluidoException;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.*;
import com.smart.appsa.repository.PedidoRepository;
import com.smart.appsa.service.BlocoService;
import com.smart.appsa.service.ExpedicaoService;
import com.smart.appsa.service.PedidoService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
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

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Bloco blocoSimples(AndarBloco andar) {
        Bloco b = new Bloco();
        b.setAndar(andar);
        b.setCorBloco(CorBloco.PRETO);
        b.setLaminas(List.of());
        return b;
    }

    private PedidoRequestDTO requestSimples() {
        return PedidoRequestDTO.builder()
                .codPedido("PED-001")
                .status(StatusPedido.PENDENTE)
                .tipoPedido(TipoPedido.SIMPLES)
                .corTampa(CorTampa.PRETO)
                .blocos(List.of(new BlocoDTO(
                        null, CorBloco.PRETO, null, AndarBloco.PRIMEIRO,
                        new Pedido(), List.of())))
                .build();
    }

    // =========================================================================
    // findAll
    // =========================================================================

    @Test
    @DisplayName("TC-01 | findAll retorna lista vazia quando não há pedidos")
    void findAll_semPedidos_retornaListaVazia() {
        when(pedidoRepository.findAll()).thenReturn(List.of());

        List<PedidoResponseDTO> resultado = pedidoService.findAll();

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("TC-02 | findAll retorna todos os pedidos existentes")
    void findAll_comPedidos_retornaTodos() {
        Pedido p = new Pedido();
        p.setId(1L);
        p.setCodPedido("PED-001");
        p.setStatus(StatusPedido.PENDENTE);
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of());
        when(pedidoRepository.findAll()).thenReturn(List.of(p));

        List<PedidoResponseDTO> resultado = pedidoService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).codPedido()).isEqualTo("PED-001");
    }

    // =========================================================================
    // findById
    // =========================================================================

    @Test
    @DisplayName("TC-03 | findById lança EntityNotFoundException para ID inexistente")
    void findById_idInexistente_lancaException() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Pedido não existe");
    }

    @Test
    @DisplayName("TC-04 | findById retorna pedido correto para ID existente")
    void findById_idExistente_retornaPedido() {
        Pedido p = new Pedido();
        p.setId(1L);
        p.setCodPedido("PED-001");
        p.setStatus(StatusPedido.PENDENTE);
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of());
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));

        PedidoResponseDTO dto = pedidoService.findById(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.codPedido()).isEqualTo("PED-001");
    }

    // =========================================================================
    // findByCodigo
    // =========================================================================

    @Test
    @DisplayName("TC-05 | findByCodigo lança EntityNotFoundException para código inexistente")
    void findByCodigo_codigoInexistente_lancaException() {
        when(pedidoRepository.findByCodPedido("INVALIDO")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.findByCodigo("INVALIDO"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("INVALIDO");
    }

    @Test
    @DisplayName("TC-06 | findByCodigo retorna pedido com código correto")
    void findByCodigo_codigoExistente_retornaPedido() {
        Pedido p = new Pedido();
        p.setId(2L);
        p.setCodPedido("PED-XYZ");
        p.setStatus(StatusPedido.PRODUCAO);
        p.setTipoPedido(TipoPedido.DUPLO);
        p.setBlocos(List.of());
        when(pedidoRepository.findByCodPedido("PED-XYZ")).thenReturn(Optional.of(p));

        PedidoResponseDTO dto = pedidoService.findByCodigo("PED-XYZ");

        assertThat(dto.codPedido()).isEqualTo("PED-XYZ");
    }

    // =========================================================================
    // findByStatus
    // =========================================================================

    @Test
    @DisplayName("TC-07 | findByStatus retorna apenas pedidos com status PENDENTE")
    void findByStatus_pendente_retornaSoPendentes() {
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
    @DisplayName("TC-08 | findByStatus retorna lista vazia quando não há pedidos com o status")
    void findByStatus_semResultados_retornaListaVazia() {
        when(pedidoRepository.findByStatus(StatusPedido.CONCLUIDO)).thenReturn(List.of());

        List<PedidoResponseDTO> resultado = pedidoService.findByStatus(StatusPedido.CONCLUIDO);

        assertThat(resultado).isEmpty();
    }

    // =========================================================================
    // updateToConcluido
    // =========================================================================

    @Test
    @DisplayName("TC-09 | updateToConcluido lança EntityNotFoundException para ID inexistente")
    void updateToConcluido_idInexistente_lancaException() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.updateToConcluido(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("TC-10 | updateToConcluido lança PedidoIsAlreadyConcluidoException se já concluído")
    void updateToConcluido_jaConluido_lancaException() {
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
    @DisplayName("TC-11 | updateToConcluido atualiza status para CONCLUIDO com sucesso")
    void updateToConcluido_statusPendente_atualizaParaConcluido() {
        Pedido p = new Pedido();
        p.setId(1L);
        p.setStatus(StatusPedido.PENDENTE);
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of());
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoResponseDTO resultado = pedidoService.updateToConcluido(1L);

        assertThat(resultado.status()).isEqualTo(StatusPedido.CONCLUIDO);
        assertThat(resultado.dataEntrada()).isNotNull();
    }

    @Test
    @DisplayName("TC-12 | updateToConcluido define dataEntrada no momento da conclusão")
    void updateToConcluido_defineDataEntradaAgora() {
        Pedido p = new Pedido();
        p.setId(5L);
        p.setStatus(StatusPedido.PRODUCAO);
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of());
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(p));
        when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        PedidoResponseDTO resultado = pedidoService.updateToConcluido(5L);
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertThat(resultado.dataEntrada()).isBetween(antes, depois);
    }

    // =========================================================================
    // validateBlocosQuantityByType
    // =========================================================================

    @Test
    @DisplayName("TC-13 | validateBlocosQuantityByType lança exceção quando lista de blocos vazia")
    void validateBlocos_listaVazia_lancaException() {
        Pedido p = new Pedido();
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of());

        assertThatThrownBy(() -> pedidoService.validateBlocosQuantityByType(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade invalida de blocos");
    }

    @Test
    @DisplayName("TC-14 | validateBlocosQuantityByType lança exceção quando blocos não batem com tipo SIMPLES")
    void validateBlocos_quantidadeErradaParaSimples_lancaException() {
        Pedido p = new Pedido();
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of(blocoSimples(AndarBloco.PRIMEIRO), blocoSimples(AndarBloco.SEGUNDO)));

        assertThatThrownBy(() -> pedidoService.validateBlocosQuantityByType(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tipo de pedido");
    }

    @Test
    @DisplayName("TC-15 | validateBlocosQuantityByType lança exceção quando blocos não batem com tipo DUPLO")
    void validateBlocos_quantidadeErradaParaDuplo_lancaException() {
        Pedido p = new Pedido();
        p.setTipoPedido(TipoPedido.DUPLO);
        p.setBlocos(List.of(blocoSimples(AndarBloco.PRIMEIRO)));

        assertThatThrownBy(() -> pedidoService.validateBlocosQuantityByType(p))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("TC-16 | validateBlocosQuantityByType aceita 3 blocos para pedido TRIPLO")
    void validateBlocos_tresBlocosParaTriplo_naoLancaException() {
        Pedido p = new Pedido();
        p.setTipoPedido(TipoPedido.TRIPLO);
        p.setBlocos(List.of(
                blocoSimples(AndarBloco.PRIMEIRO),
                blocoSimples(AndarBloco.SEGUNDO),
                blocoSimples(AndarBloco.TERCEIRO)));

        assertThatNoException().isThrownBy(() -> pedidoService.validateBlocosQuantityByType(p));
    }

    // =========================================================================
    // patch
    // =========================================================================

    @Test
    @DisplayName("TC-17 | patch lança EntityNotFoundException para ID inexistente")
    void patch_idInexistente_lancaException() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder().build();
        when(pedidoRepository.findById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.patch(50L, dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("TC-18 | patch lança IllegalArgumentException se codPedido for blank")
    void patch_codPedidoBlank_lancaException() {
        Pedido p = new Pedido();
        p.setId(1L);
        p.setStatus(StatusPedido.PENDENTE);
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of());
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));

        PedidoRequestDTO dto = PedidoRequestDTO.builder().codPedido("   ").build();

        assertThatThrownBy(() -> pedidoService.patch(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("código do pedido");
    }

    @Test
    @DisplayName("TC-19 | patch atualiza apenas campos não nulos")
    void patch_atualizaSoCamposNaoNulos() {
        Pedido p = new Pedido();
        p.setId(1L);
        p.setCodPedido("PED-OLD");
        p.setStatus(StatusPedido.PENDENTE);
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setBlocos(List.of());
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));
        when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .codPedido("PED-NEW")
                .build();

        PedidoResponseDTO resultado = pedidoService.patch(1L, dto);

        assertThat(resultado.codPedido()).isEqualTo("PED-NEW");
        assertThat(resultado.status()).isEqualTo(StatusPedido.PENDENTE); // status não mudou
    }

    // =========================================================================
    // delete
    // =========================================================================

    @Test
    @DisplayName("TC-20 | delete lança EntityNotFoundException para ID inexistente")
    void delete_idInexistente_lancaException() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("TC-21 | delete remove pedido existente sem lançar exceção")
    void delete_idExistente_removePedido() {
        Pedido p = new Pedido();
        p.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));

        assertThatNoException().isThrownBy(() -> pedidoService.delete(1L));
        verify(pedidoRepository).delete(p);
    }

    // =========================================================================
    // findByTipo
    // =========================================================================

    @Test
    @DisplayName("TC-22 | findByTipo retorna apenas pedidos do tipo DUPLO")
    void findByTipo_duplo_retornaSoDuplos() {
        Pedido p = new Pedido();
        p.setId(1L);
        p.setTipoPedido(TipoPedido.DUPLO);
        p.setStatus(StatusPedido.PENDENTE);
        p.setBlocos(List.of());
        when(pedidoRepository.findByTipoPedido(TipoPedido.DUPLO)).thenReturn(List.of(p));

        List<PedidoResponseDTO> resultado = pedidoService.findByTipo(TipoPedido.DUPLO);

        assertThat(resultado).allMatch(r -> r.tipoPedido() == TipoPedido.DUPLO);
    }

    // =========================================================================
    // findByCreationPeriod
    // =========================================================================

    @Test
    @DisplayName("TC-23 | findByCreationPeriod retorna pedidos dentro do intervalo")
    void findByCreationPeriod_intervaloValido_retornaPedidos() {
        LocalDateTime inicio = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime fim    = LocalDateTime.of(2024, 12, 31, 23, 59);

        Pedido p = new Pedido();
        p.setId(1L);
        p.setDataCriacao(LocalDateTime.of(2024, 6, 15, 10, 0));
        p.setTipoPedido(TipoPedido.SIMPLES);
        p.setStatus(StatusPedido.PENDENTE);
        p.setBlocos(List.of());
        when(pedidoRepository.findByDataCriacaoBetween(inicio, fim)).thenReturn(List.of(p));

        List<PedidoResponseDTO> resultado = pedidoService.findByCreationPeriod(inicio, fim);

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("TC-24 | findByCreationPeriod retorna lista vazia fora do intervalo")
    void findByCreationPeriod_semResultados_retornaListaVazia() {
        LocalDateTime inicio = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime fim    = LocalDateTime.of(2020, 12, 31, 23, 59);
        when(pedidoRepository.findByDataCriacaoBetween(inicio, fim)).thenReturn(List.of());

        List<PedidoResponseDTO> resultado = pedidoService.findByCreationPeriod(inicio, fim);

        assertThat(resultado).isEmpty();
    }
}
