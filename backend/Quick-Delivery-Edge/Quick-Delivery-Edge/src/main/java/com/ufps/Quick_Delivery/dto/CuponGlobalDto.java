package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.model.TipoCupon;

import java.time.LocalDate;
import java.util.UUID;

public record CuponGlobalDto(
    UUID id,
    String nombre,
    String descripcion,
    TipoCupon tipo,
    int descuentoPorcentaje,
    int descuentoEnvio,
    LocalDate fechaExpiracion,
    boolean aplicable
) {}
