package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.models.TipoVehiculo;
import java.util.UUID;

public class AuthResponse {
    private String token;
    private UUID userId;
    private String nombre;
    private String correo;
    private TipoVehiculo vehiculo;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, UUID userId, String nombre, String correo, TipoVehiculo vehiculo, String message) {
        this.token = token;
        this.userId = userId;
        this.nombre = nombre;
        this.correo = correo;
        this.vehiculo = vehiculo;
        this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public TipoVehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(TipoVehiculo vehiculo) { this.vehiculo = vehiculo; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}