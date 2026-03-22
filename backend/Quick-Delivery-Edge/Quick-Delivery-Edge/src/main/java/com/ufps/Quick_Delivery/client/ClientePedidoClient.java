package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "cliente-pedido-service", url = "${cliente-service.url}")
public interface ClientePedidoClient {

    @GetMapping("/api/pedidos/usuario/{usuarioId}/count")
    long contarPedidosUsuario(@PathVariable("usuarioId") UUID usuarioId);
}
