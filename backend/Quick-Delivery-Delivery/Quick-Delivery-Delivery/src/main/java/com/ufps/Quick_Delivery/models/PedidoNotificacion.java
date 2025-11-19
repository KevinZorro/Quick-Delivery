package com.ufps.Quick_Delivery.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pedido_notificaciones")
public class PedidoNotificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pedido_id", nullable = false)
    private UUID pedidoId;

    @Column(name = "restaurante_id", nullable = false)
    private UUID restauranteId;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "total", nullable = false)
    private Integer total;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Builder.Default
    @Column(name = "procesado")
    private Boolean procesado = false;
}

