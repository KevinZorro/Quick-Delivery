// src/main/java/com/ufps/Quick_Delivery/client/dto/DireccionResponse.java
package com.ufps.Quick_Delivery.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DireccionResponse {
    private UUID id;
    private UUID usuarioId;
    private String calle;
    private String referencia;
    private String ciudad;
    private String barrio;
    private String coordenadas; // "lat,lng"
    private String tipoReferencia;
}
