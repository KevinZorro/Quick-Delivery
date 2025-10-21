package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ufps.Quick_Delivery.dto.ProductoDto;

import java.util.UUID;

@FeignClient(name = "producto-service", url = "http://localhost:8081")
public interface ProductoClient {

    @GetMapping("/productos/{id}")
    ProductoDto getProductoById(@PathVariable("id") UUID id);
}
