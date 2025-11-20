package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ItemPedidoDto {
    private UUID productoId;
    private short cantidad;
    private int precioUnidad;
    private int subtotal;
}
