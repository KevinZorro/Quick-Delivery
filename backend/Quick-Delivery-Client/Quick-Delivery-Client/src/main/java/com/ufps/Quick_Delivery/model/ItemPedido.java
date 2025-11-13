package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entidad que representa un ítem dentro de un pedido.
 */
@Entity
@Table(name = "item_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore
    private Pedido pedido;

    @Column(name = "producto_id", nullable = false)
    private UUID productoId;

    @Column(name = "cantidad", nullable = false)
    private short cantidad;

    @Column(name = "precio_unidad", nullable = false)
    private int precioUnidad;

    @Column(name = "subtotal", nullable = false)
    private int subtotal;
}
