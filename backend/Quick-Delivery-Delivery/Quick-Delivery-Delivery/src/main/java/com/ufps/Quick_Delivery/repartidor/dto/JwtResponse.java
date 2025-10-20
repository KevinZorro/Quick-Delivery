package com.ufps.Quick_Delivery.repartidor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para la respuesta de autenticación exitosa.
 * Contiene el token JWT, el tipo de token y el email del usuario autenticado.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private String tipo = "Bearer";
    private String email;
}