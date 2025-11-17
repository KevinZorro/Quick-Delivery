package com.ufps.Quick_Delivery.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DireccionResponse {
    private UUID id;
    private String calle;
    private String referencia;
    private String ciudad;
    private String barrio;
    private String coordenadas;
    private UUID usuarioId;
    private String tipoReferencia;
}

