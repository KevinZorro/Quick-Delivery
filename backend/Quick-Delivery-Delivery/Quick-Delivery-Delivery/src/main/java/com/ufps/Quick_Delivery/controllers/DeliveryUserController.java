package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.client.ClienteClient;
import com.ufps.Quick_Delivery.dto.DeliveryUserDto;
import com.ufps.Quick_Delivery.services.DeliveryUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryUserController {

    private final DeliveryUserService service;
    private final ClienteClient clienteClient;

    @GetMapping
    public ResponseEntity<List<DeliveryUserDto>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryUserDto> getById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DeliveryUserDto> create(@Valid @RequestBody DeliveryUserDto dto) {
        DeliveryUserDto created = service.save(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryUserDto> update(@PathVariable UUID id, @Valid @RequestBody DeliveryUserDto dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para consultar ganancias
    @GetMapping("/{id}/ganancias")
    public ResponseEntity<Double> getGanancias(@PathVariable UUID id) {
        return service.findById(id)
                .map(dto -> ResponseEntity.ok(dto.getGanancias()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/ganancias")
    public ResponseEntity<Void> registrarGanancia(@PathVariable UUID id, @RequestParam double valorVenta) {
        service.registrarGanancia(id, valorVenta);
        return ResponseEntity.ok().build();
    }

    /**
     * HU023: Consultar información de contacto del cliente
     */
    @GetMapping("/pedido/{pedidoId}/contacto-cliente")
    public ResponseEntity<ClienteClient.ClienteContactoResponse> obtenerContactoCliente(
            @PathVariable UUID pedidoId,
            @RequestHeader("Authorization") String token) {
        try {
            ClienteClient.ClienteContactoResponse contacto = clienteClient.obtenerContactoClientePorPedido(pedidoId);
            return ResponseEntity.ok(contacto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

}
