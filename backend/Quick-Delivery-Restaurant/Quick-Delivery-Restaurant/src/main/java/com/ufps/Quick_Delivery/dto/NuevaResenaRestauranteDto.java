// src/main/java/com/ufps/Quick_Delivery/dto/NuevaResenaRestauranteDto.java
package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.UUID;

@Data
public class NuevaResenaRestauranteDto {
    private UUID pedidoId;
    private UUID clienteId;
    private UUID restauranteId;
    @Min(1)
    @Max(5)
    private int calificacion; // estrellas
    private String comentario;
}
