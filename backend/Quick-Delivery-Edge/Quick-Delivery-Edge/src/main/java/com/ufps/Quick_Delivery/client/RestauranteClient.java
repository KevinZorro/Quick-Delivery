package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import lombok.Data;

@FeignClient(name = "restaurante-service", url = "${restaurante-service.url}/api/restaurante")
public interface RestauranteClient {

    @PostMapping
    ResponseEntity<Void> crearRestaurante(@RequestBody RestauranteRequest request);
    
    @GetMapping("/usuario/{usuarioId}")
    ResponseEntity<RestauranteResponse> obtenerPorUsuarioId(@PathVariable("usuarioId") UUID usuarioId);
    
    @Data
    public static class RestauranteRequest {
        private UUID usuarioId;
        private String descripcion;
        private String categoria;
    }
    
    @Data
    public static class RestauranteResponse {
        private UUID id;
        private UUID usuarioId;
        private String nombre;
        private String descripcion;
        private String categoria;
    }
}
