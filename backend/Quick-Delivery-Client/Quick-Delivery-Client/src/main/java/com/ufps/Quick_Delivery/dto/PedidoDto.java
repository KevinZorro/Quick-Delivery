package com.ufps.Quick_Delivery.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDto{
    private LocalDateTime fechaPedido;
    private ZonedDateTime fechaHoraEstimada;
    private String metodoPago;
    private Integer tiempoEstimado;
    private BigDecimal total;
    private String estado;
    private String preferencias;
    private LocalDateTime fechaCreacion;
    private UUID clienteId;
    private UUID productoId;
}