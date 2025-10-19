package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa un pedido realizado por un cliente.
 * Incluye detalles como el cliente, restaurante, fechas, método de pago, total, estado, etc.
 * También contiene campos de auditoría para seguimiento de creación y actualización.
 */
@Entity
@Table(name = "pedido", schema = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido implements Serializable {

    @Id
    @NotNull(message = "El UUID del pedido no puede ser nulo")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    // Relación con Cliente (Many pedidos pueden pertenecer al mismo cliente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente"/*,nullable = false*/)
    private Cliente cliente;

    // Campo que referencia al restaurante (por ID, puede ser ajustado a ManyToOne si deseas)
    @NotNull
    @Column(name = "restaurante_id", nullable = false)
    private UUID restauranteId;
    

    // Campo que referencia al repartidor (puede ser null si aún no está asignado)
    @Column(name = "repartidor_id")
    private UUID repartidorId;

    // Relación con dirección de entrega (opcional, si tu entidad Direccion está en otro microservicio, usa UUID)
    //@NotNull(message = "La dirección de entrega no puede ser nula")
    @Column(name = "direccion_entrega_id")
    private UUID direccionEntregaId;

    // Total del pedido
    @NotNull(message = "El total no puede ser nulo")
    @Positive(message = "El total debe ser positivo")
    @Column(name = "total", precision = 10, scale = 2, nullable = false)
    private int total;

    // Fechas de creación y actualización
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Método de pago
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    // Estado del pedido
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPedido estado;

    // preferencias especiales (opcional)
    @Column(name = "preferencias", columnDefinition = "TEXT")
    private String preferencias;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
