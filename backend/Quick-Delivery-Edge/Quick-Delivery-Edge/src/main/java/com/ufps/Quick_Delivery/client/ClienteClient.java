package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import lombok.Data;

@FeignClient(name = "cliente-service", url = "http://localhost:8080/clientes") // Ajusta url y nombre
public interface ClienteClient {

    @PostMapping
    ResponseEntity<Void> crearCliente(@RequestBody ClienteRequest clienteRequest);

    // dto simple para enviar al cliente
    @Data
    public static class ClienteRequest {
        private UUID usuarioId;
    }
}
