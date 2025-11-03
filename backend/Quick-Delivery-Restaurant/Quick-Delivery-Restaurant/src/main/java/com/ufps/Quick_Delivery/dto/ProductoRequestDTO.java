package com.ufps.Quick_Delivery.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {
    
    @NotNull(message = "El ID del usuario no puede ser nulo")
    private UUID usuarioId; // ⭐ Cambiado de restauranteId a usuarioId
    
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 120, message = "El nombre no puede exceder 120 caracteres")
    private String nombre;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;
    
    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "Formato de precio inválido")
    private BigDecimal precio;
    
    @Size(max = 80, message = "La categoría no puede exceder 80 caracteres")
    private String categoria;
    
    @NotNull(message = "La disponibilidad no puede ser nula")
    private Boolean disponible;
    
    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    private String imagenUrl;
}
