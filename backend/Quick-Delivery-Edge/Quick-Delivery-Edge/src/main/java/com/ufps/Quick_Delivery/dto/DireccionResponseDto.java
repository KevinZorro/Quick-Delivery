package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.model.TipoReferencia;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DireccionResponseDto {

    private UUID id;
    private String calle;
    private String referencia;
    private String ciudad;
    private String barrio;
    private String coordenadas;
    private UUID usuario;
    private TipoReferencia tipoReferencia;
}
