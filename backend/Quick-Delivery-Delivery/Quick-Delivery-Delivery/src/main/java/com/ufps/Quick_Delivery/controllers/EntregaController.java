package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.dto.ConfirmarEntregaDto;
import com.ufps.Quick_Delivery.dto.IniciarEntregaDto;
import com.ufps.Quick_Delivery.models.Entrega;
import com.ufps.Quick_Delivery.services.EntregaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/entregas")
@RequiredArgsConstructor
public class EntregaController {

    private final EntregaService service;

    @PostMapping("/iniciar")
    public ResponseEntity<Entrega> iniciarEntrega(@RequestBody IniciarEntregaDto dto) {
        System.out.println("📝 Iniciando entrega para pedido: " + dto.getPedidoId());
        Entrega entrega = service.iniciarEntrega(dto);
        System.out.println("✅ Entrega iniciada: " + entrega.getId() + " - Estado: " + entrega.getEstado());
        return ResponseEntity.ok(entrega);
    }

    @PostMapping("/confirmar")
    public ResponseEntity<Entrega> confirmarEntrega(@RequestBody ConfirmarEntregaDto dto) {
        System.out.println("🟢 Confirmando entrega para pedido: " + dto.getPedidoId() + 
                         " - Código: " + dto.getCodigoEntrega());
        Entrega entrega = service.confirmarEntrega(dto);
        System.out.println("✅ Entrega confirmada exitosamente: " + entrega.getId() + 
                         " - Estado final: " + entrega.getEstado());
        return ResponseEntity.ok(entrega);
    }
}
