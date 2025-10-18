package com.ufps.Quick_Delivery.client;

import com.ufps.Quick_Delivery.DTO.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "restaurante-service", url = "http://localhost:8081")
public interface ProductoClient {

    @GetMapping("/productos/{id}")
    ProductoDTO getProductoById(@PathVariable("id") UUID id);
}
