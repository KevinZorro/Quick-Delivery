package com.ufps.Quick_Delivery.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    private String token;
    private String nombre;
    private String correo;
    private String rol;
}
