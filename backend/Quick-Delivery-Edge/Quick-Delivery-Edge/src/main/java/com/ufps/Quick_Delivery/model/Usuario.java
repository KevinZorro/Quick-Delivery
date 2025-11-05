package com.ufps.Quick_Delivery.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El nombre no puede ser nulo ni estar vacío")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La contraseña no puede ser nula ni estar vacía")
    @Column(nullable = false)
    private String contraseña;

    @NotBlank(message = "El correo no puede ser nulo ni estar vacío")
    @Column(nullable = false, unique = true)
    private String correo;

    @NotBlank(message = "El teléfono no puede ser nulo ni estar vacío")
    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private LocalDateTime fecharegistro;

    @Column(nullable = false)
    private boolean activo;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
}
