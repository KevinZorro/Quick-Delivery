package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ItemPedidoDto {
    private UUID productoId;
    private short cantidad;
    private double precioUnidad; // Nuevo
    private double subtotal;
}
