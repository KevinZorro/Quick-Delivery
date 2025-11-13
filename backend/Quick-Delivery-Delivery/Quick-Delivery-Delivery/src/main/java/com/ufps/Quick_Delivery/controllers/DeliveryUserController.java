package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.dto.DeliveryUserDto;
import com.ufps.Quick_Delivery.services.DeliveryUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryUserController {

    private final DeliveryUserService service;

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
}
