package com.ufps.Quick_Delivery.client;

import com.ufps.Quick_Delivery.dto.AplicarCuponGlobalRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "cupon-global-service", url = "http://localhost:8083")
public interface CuponGlobalClient {

    @PostMapping("/api/cupones-globales/aplicar")
    void aplicarCupon(@RequestBody AplicarCuponGlobalRequest request);
}
