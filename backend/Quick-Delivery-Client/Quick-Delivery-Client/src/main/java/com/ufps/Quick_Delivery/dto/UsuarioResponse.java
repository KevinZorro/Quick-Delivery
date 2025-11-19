package com.ufps.Quick_Delivery.DTO;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioResponse {
    private UUID id;
    private String nombre;    
    private String telefono;  
}
