// src/main/java/com/ufps/Quick_Delivery/dto/NuevaCalificacionRepartidorDto.java
package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;

@Data
public class NuevaCalificacionRepartidorDto {
    private UUID pedidoId;
    private UUID clienteId;
    @Min(1)
    @Max(5)
    private int calificacion; // Estrellas
    private String comentario;
}
