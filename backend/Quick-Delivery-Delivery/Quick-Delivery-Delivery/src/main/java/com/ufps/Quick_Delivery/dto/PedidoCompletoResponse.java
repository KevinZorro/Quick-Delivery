package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.client.ClienteClient;
import com.ufps.Quick_Delivery.client.ClienteDireccion;
import com.ufps.Quick_Delivery.client.ClientePedido;
import com.ufps.Quick_Delivery.client.ClienteProducto;
import lombok.Data;

import java.util.List;

@Data
public class PedidoCompletoResponse {

    private ClientePedido.PedidoResponse pedido;
    private ClienteClient.ClienteContactoResponse cliente;
    private ClienteDireccion.DireccionResponse direccionEntrega;
    private List<ClienteProducto.ProductoResponse> productos;
}
