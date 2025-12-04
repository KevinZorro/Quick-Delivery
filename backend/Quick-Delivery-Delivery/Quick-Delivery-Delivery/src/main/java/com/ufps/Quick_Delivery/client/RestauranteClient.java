package com.ufps.Quick_Delivery.Client;

import com.ufps.Quick_Delivery.dto.PedidoResponse;
import com.ufps.Quick_Delivery.dto.ItemPedidoResponse;
import com.ufps.Quick_Delivery.dto.ProductoResponse;
import com.ufps.Quick_Delivery.dto.RestauranteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "restaurante-service", url = "http://localhost:8081")
public interface RestauranteClient {

    // Productos
    @GetMapping("/api/productos/{id}")
    ProductoResponse obtenerProductoPorId(@PathVariable("id") UUID id);

    // Restaurante info
    @GetMapping("/api/restaurantes/{id}")
    ResponseEntity<RestauranteResponse> obtenerRestaurante(@PathVariable("id") UUID id);
}
