package com.ufps.Quick_Delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionPedidoDto {
    private UUID id;
    private UUID pedidoId;
    private UUID restauranteId;
    private UUID clienteId;
    private Integer total;
    private LocalDateTime fechaCreacion;
    private String direccionRestaurante;
    private String coordenadasRestaurante;
    private Double distanciaKm;
    private String distanciaTexto;
    private String tiempoEstimado;
}

