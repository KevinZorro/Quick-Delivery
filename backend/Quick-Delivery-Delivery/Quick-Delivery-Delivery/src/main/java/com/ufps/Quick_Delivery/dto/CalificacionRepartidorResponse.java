package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.models.CalificacionRepartidor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CalificacionRepartidorResponse {
    private List<CalificacionRepartidor> opiniones;
    private double promedio;
}
