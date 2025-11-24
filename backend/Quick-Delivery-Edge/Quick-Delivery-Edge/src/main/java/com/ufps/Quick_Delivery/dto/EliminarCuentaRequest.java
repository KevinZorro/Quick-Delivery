package com.ufps.Quick_Delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EliminarCuentaRequest {
    @NotBlank(message = "La contraseña es requerida")
    private String contraseña;
}

