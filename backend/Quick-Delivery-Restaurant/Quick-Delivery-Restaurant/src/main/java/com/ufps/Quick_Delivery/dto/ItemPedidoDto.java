package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ItemPedidoDto {
    private UUID productoId;
    private String nombreProducto;
    private String categoria;
    private String imagenUrl;
    private short cantidad;
    private double precioUnidad; // Nuevo
    private double subtotal;
}
