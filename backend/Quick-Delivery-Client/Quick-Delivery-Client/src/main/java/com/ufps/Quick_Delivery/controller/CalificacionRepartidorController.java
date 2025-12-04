
package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.service.CalificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class CalificacionRepartidorController {

    private final CalificacionService service;

    @PostMapping("/{pedidoId}/calificar-repartidor")
    public ResponseEntity<Void> calificarPedido(@PathVariable UUID pedidoId,
                                                @RequestParam int calificacion,
                                                @RequestParam(required = false) String comentario) {
        service.calificarRepartidor(pedidoId, calificacion, comentario);
        return ResponseEntity.ok().build();
    }
}
