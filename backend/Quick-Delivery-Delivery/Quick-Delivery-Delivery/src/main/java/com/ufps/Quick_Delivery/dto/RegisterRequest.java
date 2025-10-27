package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.models.TipoVehiculo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    @Email
    private String correo;

    @NotBlank
    @Size(min = 6)
    private String contraseña;

    @NotBlank
    @Size(max = 20)
    private String telefono;

    @NotNull
    private TipoVehiculo vehiculo;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public TipoVehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(TipoVehiculo vehiculo) { this.vehiculo = vehiculo; }
}