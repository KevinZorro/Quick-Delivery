package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "restaurante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurante {

    @Id
    @NotNull(message = "El UUID del producto no puede ser nulo")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String password; // almacenada en BCRYPT

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true; // true = cuenta activa

    @Builder.Default
    @Column(nullable = false)
    private int intentosFallidos = 0;

    // Si lockedUntil es posterior a now(), la cuenta está bloqueada temporalmente
    private LocalDateTime lockedUntil;
}
