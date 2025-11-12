package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.dto.ConfirmarEntregaRequest;
import com.ufps.Quick_Delivery.services.EntregaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/entregas")
@RequiredArgsConstructor
public class EntregaController {

    private final EntregaService entregaService;

    @PutMapping("/{repartidorId}/confirmar")
    public ResponseEntity<String> confirmarEntrega(
            @PathVariable UUID repartidorId,
            @RequestBody ConfirmarEntregaRequest request) {

        String respuesta = entregaService.confirmarEntrega(repartidorId, request);
        return ResponseEntity.ok(respuesta);
    }
}
