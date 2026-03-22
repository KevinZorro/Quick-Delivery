package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuponGlobal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCupon tipo;

    // Porcentaje de descuento (aplica a PRIMERA_COMPRA y DESCUENTO_PORCENTAJE)
    @Column(nullable = false)
    private int descuentoPorcentaje;

    // Monto fijo de descuento en envío (aplica a DESCUENTO_ENVIO)
    @Column(nullable = false)
    private int descuentoEnvio;

    private LocalDate fechaInicio;

    // null = sin expiración
    private LocalDate fechaExpiracion;

    // 0 = ilimitado
    @Column(nullable = false)
    private int usoMaximoTotal;

    @Column(nullable = false)
    private int usosActuales;

    @Column(nullable = false)
    private boolean activo;
}
