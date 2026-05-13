package com.smart.appsa.model;

import com.smart.appsa.model.enums.CorLamina;
import com.smart.appsa.model.enums.PadraoLamina;
import com.smart.appsa.model.enums.PosicaoLamina;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "lamina", check = {
    @CheckConstraint(
        name = "vl_cor_lamina",
        constraint = "vl_cor IN (1, 2, 3, 4, 5, 6)"
    ),
    @CheckConstraint(
        name = "vl_padrao_lamina",
        constraint = "vl_padrao IN (0, 1, 2, 3)"
    ),
    @CheckConstraint(
        name = "vl_posicao_lamina",
        constraint = "vl_posicao IN (1, 2, 3)"
    )
})
public class Lamina {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vl_cor")
    private CorLamina corLamina;

    @Column(name = "vl_padrao")
    private PadraoLamina padraoLamina;
    
    @Column(name = "vl_posicao")
    private PosicaoLamina posicaoLamina;

    @ManyToOne
    @JoinColumn(name = "id_bloco", nullable = false)
    private Bloco bloco;


}   
