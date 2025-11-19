package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@FeignClient(name = "restaurante-service", url = "http://localhost:8081")
public interface ClienteProducto {

    // Endpoint corregido: agrega el id a la ruta
    @GetMapping("/api/productos/{id}")
    ProductoResponse obtenerProductoPorId(@PathVariable("id") UUID id);

    @Data
    class ProductoResponse {
        private UUID id;
        private UUID restauranteId;
        private String nombre;
        private String descripcion;
        private BigDecimal precio;
        private String categoria;
        private Boolean disponible;
        private String imagenUrl;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;
        private String usuarioCreacion;
        private String usuarioActualizacion;
    }
}
