package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

/*
    * Entidad que representa un pedido realizado por un cliente.

    * Incluye detalles como el cliente, restaurante, fechas, método de pago, total, estado, etc.

    * También contiene campos de auditoría para seguimiento de creación y actualización.(por hacer)
 */

@Entity
@Table(name = "pedido_cliente", schema = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCliente implements Serializable {

    @Id
    @NotNull(message = "El UUID del pedido no puede ser nulo")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    // Relación con Cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "producto_id", nullable = false)
    private UUID producto;
    

    @NotNull(message = "La fecha del pedido no puede ser nula")
    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;

    @NotNull(message = "La fecha y hora estimada no puede ser nula")
    @Column(name = "fecha_hora_estimada", nullable = false)
    private ZonedDateTime fechaHoraEstimada;

    @NotBlank(message = "El método de pago no puede estar vacío")
    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @NotNull(message = "El tiempo estimado no puede ser nulo")
    @Column(name = "tiempo_estimado", nullable = false)
    private Integer tiempoEstimado;

    @NotNull(message = "El total no puede ser nulo")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    @NotBlank(message = "El estado no puede estar vacío")
    @Column(nullable = false)
    private String estado;

    @Column(name = "instrucciones", columnDefinition = "TEXT")
    private String instrucciones;

    @Column(name = "direccion_entrega_id")
    private UUID direccionEntregaId;

    @Column(name = "nde_pedido", nullable = false, unique = true)
    private long ndepedido;

    // Campos de auditoría
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "usuario_creacion")
    private String usuarioCreacion;

    @Column(name = "usuario_actualizacion")
    private String usuarioActualizacion;

    public boolean estaCompletado() {
        return "ENTREGADO".equals(this.estado);
    }
}