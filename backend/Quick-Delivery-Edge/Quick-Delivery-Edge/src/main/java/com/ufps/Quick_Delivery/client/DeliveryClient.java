package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import lombok.Data;

@FeignClient(name = "delivery-service", url = "https://delivery-service-192433559355.southamerica-east1.run.app")
public interface DeliveryClient {

    @PostMapping
    ResponseEntity<Void> crearDelivery(@RequestBody DeliveryRequest request);

    
    @Data
    public static class DeliveryRequest {
        private UUID usuarioId;
        private String vehiculo;
    }
}
