package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.model.EstadoPromocion;
import java.time.LocalDate;


public record PromocionUpdateRequest(
        EstadoPromocion estado,
        LocalDate fechaExpiracion,
        Integer cantidadUsos
) {}
