package com.ufps.Quick_Delivery.repartidor.dto;

import lombok.Data;
/**
 * Data Transfer Object (DTO) para manejar la solicitud de login del repartidor.
 * Contiene las credenciales necesarias para la autenticación.
 */
@Data
public class LoginRequest {
    private String email;
    private String password;
}