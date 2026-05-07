package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.model.TipoReferencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DireccionRequestDto {
    
    @NotBlank(message = "La calle es obligatoria")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9\\s\\-\\.#]{3,100}$",
        message = "La calle contiene caracteres inválidos"
    )
    private String calle;
    
    private String referencia;
    
    @NotBlank(message = "La ciudad es obligatoria")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\\s]{3,100}$",
        message = "La ciudad solo debe contener letras y espacios"
    )
    private String ciudad;
    
    @NotBlank(message = "El barrio es obligatorio")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\\s]{3,100}$",
        message = "El barrio solo debe contener letras y espacios"
    )
    private String barrio;
    
    private String coordenadas;
    
    @NotNull(message = "El tipo de referencia es obligatorio")
    private TipoReferencia tipoReferencia;
}
