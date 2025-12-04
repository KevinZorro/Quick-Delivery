package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.dto.TrackingDataResponse;
import com.ufps.Quick_Delivery.services.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    /**
     * ⭐ ENDPOINT PRINCIPAL PARA TRACKING
     * Frontend hace polling cada 10s a este endpoint
     */
    @GetMapping("/pedido/{pedidoId}/tracking-data")
    public ResponseEntity<TrackingDataResponse> obtenerTrackingData(
            @PathVariable("pedidoId") UUID pedidoId) {
        
        try {
            TrackingDataResponse data = trackingService.obtenerTrackingData(pedidoId);
            return ResponseEntity.ok(data);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
