package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

@FeignClient(name = "maps-service", url = "${edge-service.url}/api/maps")
public interface GoogleMapsClient {

    @PostMapping("/distance")
    ResponseEntity<DistanceMatrixResponse> calcularDistancia(@RequestBody DistanceMatrixRequest request);
}

