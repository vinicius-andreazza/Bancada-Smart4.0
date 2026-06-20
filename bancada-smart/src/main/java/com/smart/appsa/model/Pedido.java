package com.smart.appsa.model;

import java.time.LocalDateTime;
import java.util.List;

import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "pedido", check = {
    @CheckConstraint(
        name = "vl_status_pedido",
        constraint = "vl_status IN (1, 2, 3)"
    ),
    @CheckConstraint(
        name = "tp_pedido",
        constraint = "tp_pedido IN (1, 2, 3)"
    ),
    @CheckConstraint(
        name = "vl_tampa_pedido",
        constraint = "vl_tampa IN (1, 2, 3)"
    )
})
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long id;

    @Column(name = "cod_pedido", nullable = false)
    private Integer codPedido;

    @Column(name = "dt_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "vl_status", nullable = false)
    private StatusPedido status;

    @Column(name = "tp_pedido", nullable = false)
    private TipoPedido tipoPedido;

    @Column(name = "vl_tampa", nullable = false)
    private CorTampa corTampa;

    @Column(name = "dt_inicio", nullable = true)
    private LocalDateTime dataInicio;

    @Column(name = "dt_entrada", nullable = true)
    private LocalDateTime dataEntrada;

    @Column(name = "pos_expedicao", nullable = true)
    private Integer posExpedicao;

    @OneToMany(mappedBy = "pedido")
    private List<Bloco> blocos;
}
