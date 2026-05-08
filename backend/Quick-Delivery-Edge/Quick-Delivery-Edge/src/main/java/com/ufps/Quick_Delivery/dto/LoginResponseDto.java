package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private UUID userId;
    private String nombre;
    private String correo;
    private String rol;
    private UUID restauranteId; // ID del restaurante si el rol es RESTAURANTE
}
