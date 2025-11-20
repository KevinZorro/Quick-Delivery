package com.ufps.Quick_Delivery.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "entregas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID pedidoId;

    @Column(nullable = false)
    private UUID repartidorId;

    @Column(nullable = false)
    private String estado; // ✅ CON_EL_REPARTIDOR, EN_CAMINO_RECOGIDO, EN_CAMINO_HACIA_CLIENTE, ENTREGADO

    @Column(nullable = false)
    private String codigoConfirmacion;

    private String comentariosEntrega;

    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;

    private Long duracionMinutos;

    private String observaciones;
}
