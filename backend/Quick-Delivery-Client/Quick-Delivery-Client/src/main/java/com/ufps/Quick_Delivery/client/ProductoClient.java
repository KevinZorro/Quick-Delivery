package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.Data;
import java.util.UUID;

@FeignClient(name = "producto-service", url = "https://restaurantes-service-192433559355.southamerica-east1.run.app")
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    ProductoResponse obtenerProducto(@PathVariable("id") UUID id);

    @Data
    class ProductoResponse {
        private UUID id;
        private UUID restauranteId;
        private String nombre;
        private String descripcion;
        private Integer precio;  // ⭐ Precio del producto
        private String categoria;
        private Boolean disponible;
        private String imagenUrl;
    }
}
