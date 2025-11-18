package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.Data;
import java.util.UUID;

@FeignClient(name = "cliente-service", url = "http://localhost:8080")
public interface ClienteClient {

    @GetMapping("/clientes/{clienteId}/contacto")
    ClienteContactoResponse obtenerContactoCliente(@PathVariable("clienteId") UUID clienteId);

    @Data
    class ClienteContactoResponse {
        private UUID id;
        private String nombre;
        private String telefono;  
    }
}
