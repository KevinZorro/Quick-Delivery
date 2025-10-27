package com.ufps.Quick_Delivery.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String correo;
    private String password;
    private String rol; // CLIENTE, RESTAURANTE, REPARTIDOR
}
