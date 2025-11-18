package com.ufps.Quick_Delivery.dto;

import lombok.Data;

@Data
public class CambiarContrasenaRequest {
    private String token;
    private String nuevaContrasena;
}
