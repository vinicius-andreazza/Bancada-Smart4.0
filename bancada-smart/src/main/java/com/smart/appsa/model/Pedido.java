package com.smart.appsa.model;

import java.time.LocalDateTime;

import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumeratedValue;
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
@Table(name = "T_Pedido", check = {
    @CheckConstraint(
        name = "vl_status",
        constraint = "vl_status IN (1, 2, 3)"
    ),
    @CheckConstraint(
        name = "tp_pedido",
        constraint = "tp_pedido IN (1, 2, 3)"
    ),
    @CheckConstraint(
        name = "vl_tampa",
        constraint = "vl_tampa IN (1, 2, 3)"
    )
})
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long id;

    @Column(name = "cod_pedido")
    private String codPedido;

    @Column(name = "dt_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "vl_status")
    @EnumeratedValue
    private StatusPedido status;

    @Column(name = "tp_pedido")
    @EnumeratedValue
    private TipoPedido tipoPedido;

    @Column(name = "vl_tampa")
    @EnumeratedValue
    private CorTampa corTampa;

    @Column(name = "dt_entrada")
    private LocalDateTime dataEntrada;

    //@ManyToOne
    //private Expedicao expedicao;
}
