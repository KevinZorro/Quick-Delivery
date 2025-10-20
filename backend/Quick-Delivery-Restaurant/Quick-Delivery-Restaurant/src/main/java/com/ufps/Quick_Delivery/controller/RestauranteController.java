package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.AuthRequest;
import com.ufps.Quick_Delivery.dto.AuthResponse;
import com.ufps.Quick_Delivery.dto.CloseAccountRequest;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.service.RestauranteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurante")
public class RestauranteController {

    private final RestauranteService service;

    public RestauranteController(RestauranteService service) {
        this.service = service;
    }

        @GetMapping("/{id}")
    public ResponseEntity<Restaurante> obtenerPedido(@PathVariable UUID id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Restaurante>> listarPedidos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    //  HU032: Inicio de sesión
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        if (req.getCorreo() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(new AuthResponse("Faltan campos obligatorios"));
        }

        String result = service.attemptLogin(req.getCorreo(), req.getPassword());
        
        if (result.equals("OK")) {
            // Obtener el restaurante para devolver su UUID
            Restaurante r = service.findByCorreo(req.getCorreo()).orElseThrow();
            return ResponseEntity.ok(new AuthResponse("Acceso correcto", r.getId()));
        }
        
        return switch (result) {
            case "CUENTA_SUSPENDIDA" ->
                ResponseEntity.status(423).body(new AuthResponse("Cuenta suspendida"));
            case "CUENTA_BLOQUEADA_TEMPORALMENTE" ->
                ResponseEntity.status(423).body(new AuthResponse("Cuenta bloqueada temporalmente (intenta más tarde)"));
            default -> ResponseEntity.status(401).body(new AuthResponse("Credenciales inválidas"));
        };
    }

    //  HU034: Cerrar cuenta
    @PostMapping("/{id}/cerrar")
    public ResponseEntity<AuthResponse> cerrarCuenta(@PathVariable UUID id,
                                                      @RequestBody CloseAccountRequest req) {
        if (!req.isConfirm()) {
            return ResponseEntity.badRequest().body(new AuthResponse("Confirmación requerida"));
        }

        boolean ok = service.closeAccount(id, true);
        if (ok)
            return ResponseEntity.ok(new AuthResponse("Cuenta suspendida correctamente"));
        return ResponseEntity.status(404).body(new AuthResponse("Cuenta no encontrada"));
    }

    //  Registro (solo pruebas o nuevos restaurantes)
    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registro(@RequestBody AuthRequest req) {
        if (req.getCorreo() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(new AuthResponse("Correo y contraseña son requeridos"));
        }

        Restaurante existing = service.findByCorreo(req.getCorreo()).orElse(null);
        if (existing != null) {
            return ResponseEntity.status(409).body(new AuthResponse("Ya existe un restaurante con este correo"));
        }

        Restaurante r = service.createIfNotExists(req.getCorreo(), req.getPassword());
        return ResponseEntity.created(URI.create("/api/restaurante/" + r.getId()))
                .body(new AuthResponse("Registro exitoso"));
    }
}
