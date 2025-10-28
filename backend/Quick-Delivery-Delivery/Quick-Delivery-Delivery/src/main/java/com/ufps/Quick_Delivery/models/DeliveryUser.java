package com.ufps.Quick_Delivery.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "delivery_users")
public class DeliveryUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehiculo")
    private TipoVehiculo vehiculo;

    @Column(name = "calificacion_promedio")
    private Double calificacionPromedio = 0.0;

}