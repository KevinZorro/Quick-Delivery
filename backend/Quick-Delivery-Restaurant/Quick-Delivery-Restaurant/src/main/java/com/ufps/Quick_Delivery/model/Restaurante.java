package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
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

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean activo;

    @Column(nullable = false)
    private int intentosFallidos;

    private LocalDateTime lockedUntil;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = true)
   private String documentosLegales;

    @Column(nullable = false)
    private String tipoCocina; // string tipo cocina como antes

    @Column(nullable = true)//esto se debe cambiar a false y colocar una imagen por defecto
    private String imagenUrl;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos;
}
