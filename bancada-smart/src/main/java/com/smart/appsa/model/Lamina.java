package com.smart.appsa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "lamina")
public class Lamina {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CorLamina corLamina;

    @Enumerated(EnumType.STRING)
    private PadraoLamina padraoLamina;
    
    @Enumerated(EnumType.STRING)
    private PosicaoLamina posicaoLamina;



    
    // Getters e Setters
    public Long getId() {
        return id;
    }

    public CorLamina getCorLamina() {
        return corLamina;
    }

    public void setCorLamina(CorLamina corLamina) {
        this.corLamina = corLamina;
    }

    public PadraoLamina getPadraoLamina() {
        return padraoLamina;
    }

    public void setPadraoLamina(PadraoLamina padraoLamina) {
        this.padraoLamina = padraoLamina;
    }

    public PosicaoLamina getPosicaoLamina() {
        return posicaoLamina;
    }

    public void setPosicaoLamina(PosicaoLamina posicaoLamina) {
        this.posicaoLamina = posicaoLamina;
    }

    /*
    @ManyToOne
    @JoinColumn(name = "id_bloco", nullable = false)
    private Bloco bloco;

    public Bloco getBloco() {
        return bloco;
    }

    public void setBloco(Bloco bloco) {
        this.bloco = bloco;
    }
    */



}   
