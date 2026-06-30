package com.smart.appsa.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.enums.CorBloco;
import com.smart.appsa.repository.EstoqueRepository;
import com.smart.appsa.repository.ExpedicaoRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;


@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EstoqueRepository repository;

    private final ExpedicaoRepository expedicaoRepository;


    @Override
    public void run(String... args) throws Exception {
        if (repository.count() > 0) {
            System.out.println(">> Estoque já contém dados. Pulando inicialização.");
            return;
        }
        repository.saveAll(List.of(
            Estoque.builder().posicao(1).cor(CorBloco.AZUL.getValue()).build(),
            Estoque.builder().posicao(2).cor(CorBloco.AZUL.getValue()).build(),
            Estoque.builder().posicao(3).cor(CorBloco.AZUL.getValue()).build(),
            Estoque.builder().posicao(4).cor(CorBloco.AZUL.getValue()).build(),
            Estoque.builder().posicao(5).cor(CorBloco.VERMELHO.getValue()).build(),
            Estoque.builder().posicao(6).cor(CorBloco.VERMELHO.getValue()).build(),
            Estoque.builder().posicao(7).cor(CorBloco.PRETO.getValue()).build(),
            Estoque.builder().posicao(8).cor(CorBloco.NENHUM.getValue()).build(),
            Estoque.builder().posicao(9).cor(CorBloco.NENHUM.getValue()).build(),
            Estoque.builder().posicao(10).cor(CorBloco.NENHUM.getValue()).build(),
            Estoque.builder().posicao(11).cor(CorBloco.NENHUM.getValue()).build(),
            Estoque.builder().posicao(12).cor(CorBloco.NENHUM.getValue()).build(),
            Estoque.builder().posicao(13).cor(CorBloco.AZUL.getValue()).build(),
            Estoque.builder().posicao(14).cor(CorBloco.AZUL.getValue()).build(),
            Estoque.builder().posicao(15).cor(CorBloco.AZUL.getValue()).build(),
            Estoque.builder().posicao(16).cor(CorBloco.AZUL.getValue()).build(),
            Estoque.builder().posicao(17).cor(CorBloco.VERMELHO.getValue()).build(),
            Estoque.builder().posicao(18).cor(CorBloco.VERMELHO.getValue()).build(),
            Estoque.builder().posicao(19).cor(CorBloco.PRETO.getValue()).build(),
            Estoque.builder().posicao(20).cor(CorBloco.VERMELHO.getValue()).build(),
            Estoque.builder().posicao(21).cor(CorBloco.VERMELHO.getValue()).build(),
            Estoque.builder().posicao(22).cor(CorBloco.VERMELHO.getValue()).build(),
            Estoque.builder().posicao(23).cor(CorBloco.PRETO.getValue()).build(),
            Estoque.builder().posicao(24).cor(CorBloco.PRETO.getValue()).build(),
            Estoque.builder().posicao(25).cor(CorBloco.PRETO.getValue()).build(),
            Estoque.builder().posicao(26).cor(CorBloco.PRETO.getValue()).build(),
            Estoque.builder().posicao(27).cor(CorBloco.NENHUM.getValue()).build(),
            Estoque.builder().posicao(28).cor(CorBloco.NENHUM.getValue()).build()
        ));

        expedicaoRepository.saveAll(List.of(
            Expedicao.builder().posicao(1).pedido(null).build(),
            Expedicao.builder().posicao(2).pedido(null).build(),
            Expedicao.builder().posicao(3).pedido(null).build(),
            Expedicao.builder().posicao(4).pedido(null).build(),
            Expedicao.builder().posicao(5).pedido(null).build(),
            Expedicao.builder().posicao(6).pedido(null).build(),
            Expedicao.builder().posicao(7).pedido(null).build(),
            Expedicao.builder().posicao( 8).pedido(null).build(),
            Expedicao.builder().posicao(9).pedido(null).build(),
            Expedicao.builder().posicao(10).pedido(null).build(),
            Expedicao.builder().posicao(11).pedido(null).build(),
            Expedicao.builder().posicao(12).pedido(null).build()
        ));

        System.out.println(">> Dados iniciais carregados com sucesso!");
    }
}