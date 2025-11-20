package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.dto.ConfirmarEntregaDto;
import com.ufps.Quick_Delivery.dto.IniciarEntregaDto;
import com.ufps.Quick_Delivery.dto.RegistrarTiempoEntregaDto;
import com.ufps.Quick_Delivery.models.Entrega;
import com.ufps.Quick_Delivery.services.EntregaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/entregas")
@RequiredArgsConstructor
public class EntregaController {

    private final EntregaService service;

    @PostMapping("/iniciar")
    public ResponseEntity<Entrega> iniciarEntrega(@RequestBody IniciarEntregaDto dto) {
        return ResponseEntity.ok(service.iniciarEntrega(dto));
    }

    @PostMapping("/confirmar")
    public ResponseEntity<Entrega> confirmarEntrega(@RequestBody ConfirmarEntregaDto dto) {
        return ResponseEntity.ok(service.confirmarEntrega(dto));
    }
}
