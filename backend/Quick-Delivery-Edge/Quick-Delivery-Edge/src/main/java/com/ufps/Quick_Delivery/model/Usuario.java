package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol; // CLIENTE, RESTAURANTE, REPARTIDOR

    private LocalDateTime lastLogin;
    

    private boolean activo = true;
    private int intentosFallidos = 0;
    private LocalDateTime lockedUntil;
}
