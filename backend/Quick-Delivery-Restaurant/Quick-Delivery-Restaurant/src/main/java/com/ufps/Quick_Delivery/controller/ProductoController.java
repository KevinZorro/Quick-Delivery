package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.dto.ProductoDTO;
import com.ufps.Quick_Delivery.model.Producto;
import com.ufps.Quick_Delivery.service.ProductoService;
import com.ufps.Quick_Delivery.service.RestauranteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/productos")
@RequiredArgsConstructor
@CrossOrigin
public class ProductoController {

    private final ProductoService service;
    private final RestauranteService restauranteService;

    @GetMapping
    public List<Producto> getAllPedidos() {
        return service.getAllPedidos();
    }

    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<Producto>> listarPorRestaurante(@PathVariable("restauranteId") UUID restauranteId) {
        return ResponseEntity.ok(service.findByRestaurante(restauranteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(service.findByUuidProducto(id));
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoDTO req) {
        Optional<Restaurante> restauranteOpt = restauranteService.buscarPorId(req.getRestauranteId());
        if (restauranteOpt.isEmpty()) {
            System.err.println("Restaurante no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Producto p = new Producto();
        p.setRestaurante(restauranteOpt.get());
        p.setNombre(req.getNombre());
        p.setDescripcion(req.getDescripcion());
        p.setPrecio(req.getPrecio());
        p.setCategoria(req.getCategoria());
        p.setDisponible(req.getDisponible() != null ? req.getDisponible() : Boolean.TRUE);
        p.setImagenUrl(req.getImagenUrl());
        Producto creado = service.create(p);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable UUID id,
            @Valid @RequestBody ProductoDTO request) {
        Producto p = new Producto();
        p.setNombre(request.getNombre());
        p.setDescripcion(request.getDescripcion());
        p.setPrecio(request.getPrecio());
        p.setCategoria(request.getCategoria());
        p.setDisponible(request.getDisponible() != null ? request.getDisponible() : Boolean.TRUE);
        p.setImagenUrl(request.getImagenUrl());
        return ResponseEntity.ok(service.update(id, p));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/restaurante/{restauranteId}/buscar")
    public ResponseEntity<List<Producto>> buscarNombre(@PathVariable UUID restauranteId,
            @RequestParam String nombre) {
        return ResponseEntity.ok(service.buscarPorNombre(restauranteId, nombre));
    }

    @GetMapping("/restaurante/{restauranteId}/precio")
    public ResponseEntity<List<Producto>> filtrarPrecio(@PathVariable UUID restauranteId,
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return ResponseEntity.ok(service.filtrarPorPrecio(restauranteId, min, max));
    }

    @GetMapping("/restaurante/{restauranteId}/categoria/{categoria}")
    public ResponseEntity<List<Producto>> porCategoria(@PathVariable UUID restauranteId,
            @PathVariable String categoria) {
        return ResponseEntity.ok(service.porCategoria(restauranteId, categoria));
    }
}
