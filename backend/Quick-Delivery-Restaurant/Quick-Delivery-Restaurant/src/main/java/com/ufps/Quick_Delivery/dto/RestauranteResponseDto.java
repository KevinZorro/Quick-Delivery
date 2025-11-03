package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.model.Categoria;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestauranteResponseDto {

    private UUID id;
    private UUID usuarioId;
    private String descripcion;
    private Categoria categoria;
    private Double calificacionPromedio;
    private String imagenUrl;
}
