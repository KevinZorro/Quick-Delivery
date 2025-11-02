package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import lombok.Data;

@FeignClient(name = "cliente-service", url = "http://localhost:8080/clientes")
public interface ClienteClient {

    @PostMapping
    ResponseEntity<Void> crearCliente(@RequestBody ClienteRequest clienteRequest);

    // ⭐ NUEVO MÉTODO: Obtener cliente por usuarioId
    @GetMapping("/por-usuario/{usuarioId}")
    ClienteResponse obtenerClientePorUsuarioId(@PathVariable("usuarioId") UUID usuarioId);

    // DTO para crear cliente
    @Data
    class ClienteRequest {
        private UUID usuarioId;
    }

    // ⭐ NUEVO DTO: Respuesta del cliente
    @Data
    class ClienteResponse {
        private UUID id;
        private UUID usuarioId;
    }
}
