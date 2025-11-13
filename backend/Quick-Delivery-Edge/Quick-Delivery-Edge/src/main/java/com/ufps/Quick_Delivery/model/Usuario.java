package com.ufps.Quick_Delivery.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "El nombre no puede ser nulo ni estar vacío")
    private String nombre;

    @NotBlank(message = "La contraseña no puede ser nula ni estar vacía")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$",
        message = "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número"
    )
    private String contraseña;

    @NotBlank(message = "El correo no puede ser nulo ni estar vacío")
    private String correo;

    @NotBlank(message = "El teléfono no puede ser nulo ni estar vacío")
    private String telefono;

    private LocalDateTime fecharegistro;

    private boolean activo;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    private Rol rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Direccion> direcciones = new ArrayList<>();
}
