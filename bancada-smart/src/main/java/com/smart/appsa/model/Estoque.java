package com.smart.appsa.model;


import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "estoque",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "posicao")
    },
    check = {
        @CheckConstraint(
            name = "vl_cor_estoque",
            constraint = "vl_cor IN (0,1,2,3)"
        )
    }
)
public class Estoque {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @Column(name = "posicao", nullable = false)
    private Integer posicao;

    @Column(name = "vl_cor", nullable = false)
    private Integer cor;
}
