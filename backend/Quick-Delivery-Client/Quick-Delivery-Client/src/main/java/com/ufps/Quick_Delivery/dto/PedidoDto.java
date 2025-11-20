package com.ufps.Quick_Delivery.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Builder;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDto {
    private UUID id;             // ← AÑADIDO
    private UUID restauranteId;  // ← AÑADIDO
    private UUID clienteId;

    private LocalDateTime fechaPedido;
    private ZonedDateTime fechaHoraEstimada;
    private String metodoPago;
    private Integer tiempoEstimado;
    private BigDecimal total;
    private String estado;
    private String preferencias;
    private LocalDateTime fechaCreacion;

    private UUID productoId;
}

