package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.model.TipoReferencia;
import lombok.Data;

import java.util.UUID;

@Data
public class DireccionResponseDto {
    private UUID id;
    private UUID usuarioId;
    private String calle;
    private String referencia;
    private String ciudad;
    private String barrio;
    private String coordenadas;
    private TipoReferencia tipoReferencia;
}
