package com.ufps.Quick_Delivery.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
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

    private UUID id;
    private UUID restauranteId;
    private UUID clienteId;

    private LocalDateTime fechaPedido;
    private ZonedDateTime fechaHoraEstimada;
    private String metodoPago;
    private Integer tiempoEstimado;
    private BigDecimal total;
    private String estado;
    private String preferencias;
    private LocalDateTime fechaCreacion;

    // ⭐ ESTA ES LA PARTE QUE FALTABA
    private List<ItemPedidoDto> items;
}
