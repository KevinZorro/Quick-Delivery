package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

/**
 * DTO para transferir información del ítem de un pedido.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDto {

    private UUID id;
    private UUID pedidoId;
    private UUID productoId;
    private short cantidad;
    private int precioUnidad;
    private int subtotal;
    private String preferencias;
}
