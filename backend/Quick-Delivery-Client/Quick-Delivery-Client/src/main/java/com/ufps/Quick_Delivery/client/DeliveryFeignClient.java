
package com.ufps.Quick_Delivery.client;

import com.ufps.Quick_Delivery.dto.NuevaCalificacionRepartidorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@FeignClient(name = "delivery-service", url = "http://localhost:8082")
public interface DeliveryFeignClient {
    @PostMapping("/api/delivery/{repartidorId}/calificaciones")
    void enviarCalificacion(@PathVariable("repartidorId") UUID repartidorId,
                            @RequestBody NuevaCalificacionRepartidorDto dto);
}
