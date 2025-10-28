package com.ufps.Quick_Delivery.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

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
}
