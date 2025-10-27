package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.model.Categoria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteRequest {
    private UUID usuarioId;
    private String descripcion;
    private Categoria categoria;
    private Double calificacionPromedio;
    private String imagenUrl;
}
