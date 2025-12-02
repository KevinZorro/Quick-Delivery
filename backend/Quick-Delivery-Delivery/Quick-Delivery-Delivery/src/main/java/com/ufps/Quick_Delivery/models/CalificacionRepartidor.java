// src/main/java/com/ufps/Quick_Delivery/models/CalificacionRepartidor.java
package com.ufps.Quick_Delivery.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "calificacion_repartidor")
public class CalificacionRepartidor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID repartidorId;

    @Column(nullable = false)
    private UUID clienteId;

    @Column(nullable = false)
    private UUID pedidoId;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int calificacion; // Solo 1-5 estrellas

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
}
