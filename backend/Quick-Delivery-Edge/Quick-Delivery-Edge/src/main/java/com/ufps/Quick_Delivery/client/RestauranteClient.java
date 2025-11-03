package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import lombok.Data;

@FeignClient(name = "restaurante-service", url = "http://localhost:8081/api/restaurante")
public interface RestauranteClient {

    @PostMapping
    ResponseEntity<Void> crearRestaurante(@RequestBody RestauranteRequest request);
    @Data
    public static class RestauranteRequest {
        private UUID usuarioId;
        private String descripcion;
        private String categoria;
        
    }
}
