package com.ufps.Quick_Delivery.model;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "codigo", nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(name = "descuento_porcentaje", nullable = false)
    private short descuentoPorcentaje;

    @Column(name = "fecha_expiracion", nullable = true)
    private LocalDate fechaExpiracion;

    @Column(name = "cantidad_usos", nullable = true)
    private int cantidadUsos;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPromocion estado;

    @Column(name = "restaurante_id", nullable = true)
    private UUID restauranteId;
}
