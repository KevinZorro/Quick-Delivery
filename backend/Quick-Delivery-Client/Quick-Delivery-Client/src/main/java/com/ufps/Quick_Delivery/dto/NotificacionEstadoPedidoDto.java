package com.ufps.Quick_Delivery.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificacionEstadoPedidoDto {
    private UUID pedidoId;
    private UUID clienteId;
    private UUID usuarioId;
    private UUID restauranteId;
    private String estado;
    private String titulo;
    private String mensaje;
    private LocalDateTime fecha;
}
