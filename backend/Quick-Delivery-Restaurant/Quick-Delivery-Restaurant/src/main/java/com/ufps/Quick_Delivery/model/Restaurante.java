package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String password; // almacenada en BCRYPT

    @Column(nullable = false)
    private boolean activo = true; // true = cuenta activa

    @Column(nullable = false)
    private int intentosFallidos = 0;

// Nefer Estas son las que voy a usar para HU31
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String documentosLegales;

    @Column(nullable = false)
    private String tipoCocina;
// Hasta aqui va HU31  

    // Si lockedUntil es posterior a now(), la cuenta está bloqueada temporalmente
    private LocalDateTime lockedUntil;
}
