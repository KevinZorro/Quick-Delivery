package com.ufps.Quick_Delivery.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedido_notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pedido_id", nullable = false, unique = true)
    private UUID pedidoId;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "restaurante_id", nullable = false)
    private UUID restauranteId;

    @Column(name = "direccion_entrega_id", nullable = false)
    private UUID direccionEntregaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    @Column(name = "repartidor_id")
    private UUID repartidorId; // Se asigna cuando un repartidor acepta

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_aceptacion")
    private LocalDateTime fechaAceptacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoNotificacion.PENDIENTE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.estado == EstadoNotificacion.ACEPTADO && this.fechaAceptacion == null) {
            this.fechaAceptacion = LocalDateTime.now();
        }
    }
}

