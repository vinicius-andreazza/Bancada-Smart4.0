package com.smart.appsa.model;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "T_Bloco", check = {
    @CheckConstraint(
        name = "vl_cor",
        constraint = "vl_cor IN (1, 2, 3)"
    )
})
public class Bloco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bloco")
    private Long id;

    
    @Column(name = "vl_cor")
    private Integer vl_cor;

    //@ManyToOne()
    //private Estoque posEstoque;

    //@ManyToOne()
    //private Estoque posEstoque;
}
