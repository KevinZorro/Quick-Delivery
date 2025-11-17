package com.ufps.Quick_Delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDto {
    private UUID id;
    private UUID productoId;
    private Integer cantidad;
    private Integer precioUnidad;
    private Integer subtotal;
}

