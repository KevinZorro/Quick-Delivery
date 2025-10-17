package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/*
 * Entidad que representa un producto ofrecido por un restaurante.
 * Incluye detalles como nombre, descripción, precio, categoría,
 * disponibilidad e imagen, además de campos de auditoría.
 */

@Entity
@Table(name = "producto", schema = "restaurante")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto implements Serializable {

    @Id
    @NotNull(message = "El UUID del producto no puede ser nulo")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "restaurante_id", referencedColumnName = "id")
    private Restaurante restaurante;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 120, message = "El nombre no puede exceder 120 caracteres")
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "Formato de precio inválido")
    @Column(name = "precio", precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;

    @Size(max = 80, message = "La categoría no puede exceder 80 caracteres")
    @Column(name = "categoria", length = 80)
    private String categoria;

    @NotNull(message = "La disponibilidad no puede ser nula")
    @Column(name = "disponible", nullable = false)
    private Boolean disponible;

    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    // Auditoría
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "usuario_creacion")
    private String usuarioCreacion;

    @Column(name = "usuario_actualizacion")
    private String usuarioActualizacion;

    @PrePersist
    void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.disponible == null) this.disponible = Boolean.TRUE;
        if (this.id == null) this.id = UUID.randomUUID();
    }

    @PreUpdate
    void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean estaDisponible() {
        return Boolean.TRUE.equals(this.disponible);
    }
}
