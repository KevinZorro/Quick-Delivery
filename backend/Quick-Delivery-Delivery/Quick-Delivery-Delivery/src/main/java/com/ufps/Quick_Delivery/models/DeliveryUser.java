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

    @Builder.Default
    @Column(name = "calificacion_promedio")
    private Double calificacionPromedio = 0.0;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Builder.Default
    @Column(name = "rango_km")
    private Double rangoKm = 10.0; // Rango por defecto de 10 km

}