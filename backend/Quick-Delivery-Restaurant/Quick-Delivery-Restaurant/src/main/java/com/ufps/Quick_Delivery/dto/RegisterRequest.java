package com.ufps.Quick_Delivery.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String direccion;
    private String telefono;
    private String correo;
    private String password;
    private String tipoCocina;
    private String documentosLegales;
}
