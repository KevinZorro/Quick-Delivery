package com.ufps.Quick_Delivery.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/*
 * DTO para transferir datos de productos entre capas de la aplicación.
 * Incluye validaciones para asegurar la integridad de los datos.
 */
@Data
public class ProductoDTO {
    
    @NotNull
    private UUID restauranteId;

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal precio;

    private String categoria;

    private Boolean disponible;

    private String imagenUrl;

    // opcional: usuario que crea/actualiza
    private String usuario;
}
