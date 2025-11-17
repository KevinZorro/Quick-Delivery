package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "direccion-service", url = "${edge-service.url}/api/direcciones")
public interface DireccionClient {

    @GetMapping("/{id}")
    ResponseEntity<DireccionResponse> obtenerDireccion(@PathVariable("id") UUID id);

    @GetMapping("/usuario/{usuarioId}")
    ResponseEntity<List<DireccionResponse>> obtenerDireccionesPorUsuario(@PathVariable("usuarioId") UUID usuarioId);
}

