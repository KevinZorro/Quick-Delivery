package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.model.TipoReferencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DireccionRequestDto {
    
    @NotBlank(message = "La calle es obligatoria")
    private String calle;
    
    private String referencia;
    
    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;
    
    @NotBlank(message = "El barrio es obligatorio")
    private String barrio;
    
    private String coordenadas;
    
    @NotNull(message = "El tipo de referencia es obligatorio")
    private TipoReferencia tipoReferencia;
}
