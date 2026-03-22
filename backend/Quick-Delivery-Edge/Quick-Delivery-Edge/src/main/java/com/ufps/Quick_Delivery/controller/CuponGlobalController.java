package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.AplicarCuponRequest;
import com.ufps.Quick_Delivery.dto.CuponGlobalDto;
import com.ufps.Quick_Delivery.model.CuponGlobal;
import com.ufps.Quick_Delivery.service.CuponGlobalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cupones-globales")
@RequiredArgsConstructor
public class CuponGlobalController {

    private final CuponGlobalService cuponGlobalService;

    // Cupones disponibles para un cliente (con flag aplicable)
    @GetMapping("/disponibles")
    public ResponseEntity<List<CuponGlobalDto>> disponibles(
            @RequestParam UUID clienteId) {
        return ResponseEntity.ok(cuponGlobalService.listarDisponibles(clienteId));
    }

    // Listar todos (admin)
    @GetMapping
    public ResponseEntity<List<CuponGlobal>> listarTodos() {
        return ResponseEntity.ok(cuponGlobalService.listarTodos());
    }

    // Crear cupón (admin)
    @PostMapping
    public ResponseEntity<CuponGlobal> crear(@RequestBody CuponGlobal cupon) {
        return ResponseEntity.ok(cuponGlobalService.crear(cupon));
    }

    // Aplicar cupón al confirmar pedido (llamado por el servicio Cliente via Feign)
    @PostMapping("/aplicar")
    public ResponseEntity<?> aplicar(@RequestBody AplicarCuponRequest request) {
        try {
            cuponGlobalService.aplicar(request);
            return ResponseEntity.ok(Map.of("mensaje", "Cupón aplicado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Eliminar cupón (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        cuponGlobalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
