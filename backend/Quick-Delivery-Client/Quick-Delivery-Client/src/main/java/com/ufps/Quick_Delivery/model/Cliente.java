package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "cliente", schema = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente implements Serializable {

    @Id
    @NotNull(message = "El UUID del cliente no puede ser nulo")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Column(nullable = false, length = 30)
    private String telefono;

    @NotBlank(message = "El email no puede estar vacío")
    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @NotNull(message = "El estado activo no puede ser nulo")
    @Column(nullable = false)
    private Boolean activo = true;
}
