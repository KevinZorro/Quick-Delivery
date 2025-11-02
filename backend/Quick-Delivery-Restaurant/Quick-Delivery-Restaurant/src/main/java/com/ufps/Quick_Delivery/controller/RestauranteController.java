package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.AuthResponse;
import com.ufps.Quick_Delivery.dto.RestauranteRequest;
import com.ufps.Quick_Delivery.model.Producto;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.service.ProductoService;
import com.ufps.Quick_Delivery.service.RestauranteService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurante")
public class RestauranteController {

    private final RestauranteService service;
    private final ProductoService productoService;

    public RestauranteController(RestauranteService restauranteService, ProductoService productoService) {
        this.service = restauranteService;
        this.productoService = productoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurante> obtenerRestaurante(@PathVariable("id") UUID id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Restaurante>> listarRestaurantes() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Restaurante>> listarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.listarPorUsuarioId(usuarioId));
    }

    @PostMapping
    public ResponseEntity<Restaurante> crearRestaurante(@Valid @RequestBody Restaurante restaurante) {
        Restaurante creado = service.crear(restaurante);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthResponse> actualizarRestaurante(
            @PathVariable UUID id,
            @RequestBody RestauranteRequest req) {
        try {
            Restaurante actualizado = service.actualizar(id, req);

            if (actualizado != null) {
                return ResponseEntity.ok(new AuthResponse("Restaurante actualizado correctamente"));
            }

            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new AuthResponse("Error al actualizar restaurante"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AuthResponse> eliminarRestaurante(@PathVariable UUID id) {
        boolean eliminado = service.eliminar(id);

        if (eliminado) {
            return ResponseEntity.ok(new AuthResponse("Restaurante eliminado correctamente"));
        }

        return ResponseEntity.status(404).body(new AuthResponse("Restaurante no encontrado"));
    }

    @PutMapping("/{id}/calificacion")
    public ResponseEntity<AuthResponse> actualizarCalificacion(
            @PathVariable UUID id,
            @RequestParam Double nuevaCalificacion) {
        try {
            boolean actualizado = service.actualizarCalificacion(id, nuevaCalificacion);

            if (actualizado) {
                return ResponseEntity.ok(new AuthResponse("Calificación actualizada"));
            }

            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new AuthResponse("Error al actualizar calificación"));
        }
    }


@GetMapping("/{id}/productos")
public ResponseEntity<List<Producto>> listarProductos(@PathVariable("id") UUID id) {
    if (service.buscarPorId(id).isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    List<Producto> productos = productoService.findByRestaurante(id);
    return ResponseEntity.ok(productos);
}

}
