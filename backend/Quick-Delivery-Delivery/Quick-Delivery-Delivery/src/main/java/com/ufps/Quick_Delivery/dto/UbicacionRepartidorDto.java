package com.ufps.Quick_Delivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionRepartidorDto {
    
    @NotNull(message = "La latitud es requerida")
    private Double latitud;

    @NotNull(message = "La longitud es requerida")
    private Double longitud;

    /**
     * Rango máximo en kilómetros (opcional, por defecto 10 km)
     */
    private Double rangoMaximoKm = 10.0;
}

