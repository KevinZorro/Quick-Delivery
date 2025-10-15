package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.ProductoDTO;
import com.ufps.Quick_Delivery.model.Producto;
import com.ufps.Quick_Delivery.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@CrossOrigin
public class ProductoController {
    
    private final ProductoService service;

    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<Producto>> listarPorRestaurante(@PathVariable UUID restauranteId) {
        return ResponseEntity.ok(service.findByRestaurante(restauranteId));
    }

    @GetMapping("/{productoId}")
    public ResponseEntity<Producto> obtener(@PathVariable UUID productoId) {
        return ResponseEntity.ok(service.findByUuidProducto(productoId));
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoDTO req) {
        Producto p = new Producto();
        p.setProductoId(UUID.randomUUID());
        p.setRestauranteId(req.getRestauranteId());
        p.setNombre(req.getNombre());
        p.setDescripcion(req.getDescripcion());
        p.setPrecio(req.getPrecio());
        p.setCategoria(req.getCategoria());
        p.setDisponible(req.getDisponible() != null ? req.getDisponible() : Boolean.TRUE);
        p.setImagenUrl(req.getImagenUrl());
        return new ResponseEntity<>(service.create(p), HttpStatus.CREATED);
    }

    @PutMapping("/{productoId}")
    public ResponseEntity<Producto> actualizar(@PathVariable UUID productoId,
                                                          @Valid @RequestBody ProductoDTO request) {
        Producto p = new Producto();
        p.setProductoId(productoId);
        p.setRestauranteId(request.getRestauranteId());
        p.setNombre(request.getNombre());
        p.setDescripcion(request.getDescripcion());
        p.setPrecio(request.getPrecio());
        p.setCategoria(request.getCategoria());
        p.setDisponible(request.getDisponible() != null ? request.getDisponible() : Boolean.TRUE);
        p.setImagenUrl(request.getImagenUrl());
        return ResponseEntity.ok(service.update(productoId, p));
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID productoId) {
        service.delete(productoId);
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

    @GetMapping("/restaurante/{restauranteId}/disponibles")
    public ResponseEntity<List<Producto>> disponibles(@PathVariable UUID restauranteId) {
        return ResponseEntity.ok(service.disponibles(restauranteId));
    }
}
