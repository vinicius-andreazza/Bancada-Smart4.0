package com.smart.appsa.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.smart.appsa.model.enums.AndarBloco;
import com.smart.appsa.model.enums.CorBloco;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bloco", check = {
    @CheckConstraint(
        name = "vl_cor_bloco",
        constraint = "vl_cor IN (1, 2, 3)"
    )
})
public class Bloco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bloco")
    private Long id;

    
    @Column(name = "vl_cor", nullable = false)
    private CorBloco corBloco;

    @Column(name="pos_estoque", nullable = false)
    private Integer posEstoque;

    @Column(name="andar", nullable = false)
    private AndarBloco andar;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    @ToString.Exclude
    private Pedido pedido;

    @OneToMany(mappedBy = "bloco")
    @JsonBackReference
    private List<Lamina> laminas;
}
