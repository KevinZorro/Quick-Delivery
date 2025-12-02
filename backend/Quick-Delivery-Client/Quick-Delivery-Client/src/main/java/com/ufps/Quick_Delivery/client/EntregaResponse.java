package com.ufps.Quick_Delivery.client;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class EntregaResponse {
    private UUID id;
    private UUID pedidoId;
    private UUID repartidorId;
    private String estado;
    private LocalDateTime horaInicio;
    private String codigoConfirmacion;
}
