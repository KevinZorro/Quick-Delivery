package com.ufps.Quick_Delivery.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestauranteResponse {
    private UUID id;
    private UUID usuarioId;
    private String descripcion;
    private String categoria;
    private Double calificacionPromedio;
    private String imagenUrl;
}

