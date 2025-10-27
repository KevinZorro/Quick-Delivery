package com.ufps.Quick_Delivery.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "delivery_users")
public class DeliveryUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    @Email
    @Column(unique = true)
    private String correo;

    @NotBlank
    @Size(min = 6)
    private String contraseña;

    @NotBlank
    @Size(max = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehiculo")
    private TipoVehiculo vehiculo;

    @Column(name = "calificacion_promedio")
    private Double calificacionPromedio = 0.0;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "activo")
    private Boolean activo = true;

    public DeliveryUser() {}

    public DeliveryUser(String nombre, String correo, String contraseña, String telefono, TipoVehiculo vehiculo) {
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña = contraseña;
        this.telefono = telefono;
        this.vehiculo = vehiculo;
        this.activo = true;
        this.calificacionPromedio = 0.0;
    }

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
    public Double getCalificacionPromedio() { return calificacionPromedio; }
    public void setCalificacionPromedio(Double calificacionPromedio) { this.calificacionPromedio = calificacionPromedio; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}