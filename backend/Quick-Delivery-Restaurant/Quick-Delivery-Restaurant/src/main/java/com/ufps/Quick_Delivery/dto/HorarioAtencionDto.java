package com.ufps.Quick_Delivery.dto;


import lombok.*;
import java.time.LocalTime;
import java.util.UUID;
import com.ufps.Quick_Delivery.model.DiaSemana;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioAtencionDto {
    private UUID id;
    private DiaSemana diaSemana;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private UUID restauranteId;
}