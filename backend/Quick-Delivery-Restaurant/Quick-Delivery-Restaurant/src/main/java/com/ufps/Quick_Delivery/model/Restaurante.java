package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "restaurante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID usuarioId;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    private Double calificacionPromedio;

    private String imagenUrl;
}
