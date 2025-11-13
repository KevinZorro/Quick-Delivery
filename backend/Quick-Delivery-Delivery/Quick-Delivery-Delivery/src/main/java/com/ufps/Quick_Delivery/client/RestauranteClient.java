package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.Data;

import java.util.UUID;

@FeignClient(name = "restaurante-service", url = "${restaurante-service.url}")
public interface RestauranteClient {

    @GetMapping("/api/restaurante/{id}")
    RestauranteResponse obtenerRestaurante(@PathVariable("id") UUID id);

    @Data
    class RestauranteResponse {
        private UUID id;
        private UUID usuarioId;
        private String descripcion;
        private String categoria;
        private Double calificacionPromedio;
        private String imagenUrl;
    }
}

