package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.Data;
import java.util.UUID;

@FeignClient(name = "cliente-service", url = "http://localhost:8080")
public interface ClienteClient {

    @GetMapping("/api/pedidos/{pedidoId}/contacto-cliente")
    ClienteContactoResponse obtenerContactoClientePorPedido(@PathVariable("pedidoId") UUID pedidoId);

    @Data
    class ClienteContactoResponse {
        private UUID clienteId;
        private String nombreCompleto;
        private String telefono;
        private String direccionEntrega;
        private String referenciaEntrega;
    }
}
