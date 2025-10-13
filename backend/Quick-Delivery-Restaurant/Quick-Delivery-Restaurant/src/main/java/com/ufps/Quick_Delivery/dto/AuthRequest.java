package com.ufps.Quick_Delivery.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String correo;
    private String password;
}
