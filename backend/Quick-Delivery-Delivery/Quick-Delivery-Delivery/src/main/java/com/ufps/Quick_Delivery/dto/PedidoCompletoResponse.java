package com.ufps.Quick_Delivery.dto;
import lombok.Data;

import java.util.List;

@Data
public class PedidoCompletoResponse {

    private PedidoResponse pedido;
    private ClienteContactoResponse cliente;
    private DireccionResponse direccionEntrega;
    private List<ProductoResponse> productos;

}
