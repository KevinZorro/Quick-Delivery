package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.dto.ConfirmarEntregaRequest;
import com.ufps.Quick_Delivery.models.Entrega;
import com.ufps.Quick_Delivery.services.EntregaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/entregas")
@RequiredArgsConstructor
public class EntregaController {

    private final EntregaService entregaService;

    // ✅ Confirmar entrega
    @PutMapping("/{repartidorId}/confirmar")
    public ResponseEntity<String> confirmarEntrega(
            @PathVariable("repartidorId") UUID repartidorId,  // <--- nombre explícito
            @RequestBody ConfirmarEntregaRequest request) {

        String respuesta = entregaService.confirmarEntrega(repartidorId, request);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping
public ResponseEntity<?> listarEntregas() {
    return ResponseEntity.ok(entregaService.obtenerTodas());
}

@DeleteMapping("/{id}")
public ResponseEntity<String> eliminarEntrega(@PathVariable("id") UUID id) {
    entregaService.eliminarEntrega(id);
    return ResponseEntity.ok("Entrega eliminada correctamente");
}

}

