package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RegistrarTiempoEntregaDto {
    private UUID pedidoId;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private String observaciones; // opcional
}