package com.ufps.Quick_Delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAtencionRequest {
    private UUID restauranteId;
    private DayOfWeek diaSemana;
    private LocalTime apertura;
    private LocalTime cierre;
}
