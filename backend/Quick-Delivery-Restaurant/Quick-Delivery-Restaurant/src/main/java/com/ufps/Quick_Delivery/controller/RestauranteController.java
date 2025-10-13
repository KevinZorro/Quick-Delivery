package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.AuthRequest;
import com.ufps.Quick_Delivery.dto.AuthResponse;
import com.ufps.Quick_Delivery.dto.CloseAccountRequest;
import com.ufps.Quick_Delivery.dto.RegisterRequest;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.service.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/restaurante")
public class RestauranteController {

    private final RestauranteService service;

    @Autowired
    public RestauranteController(RestauranteService service) {
        this.service = service;
    }

    // 🔐 HU032: Inicio de sesión
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        if (req.getCorreo() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(new AuthResponse("Faltan campos obligatorios"));
        }

        String result = service.attemptLogin(req.getCorreo(), req.getPassword());
        return switch (result) {
            case "OK" -> ResponseEntity.ok(new AuthResponse("Acceso correcto"));
            case "CUENTA_SUSPENDIDA" ->
                    ResponseEntity.status(423).body(new AuthResponse("Cuenta suspendida"));
            case "CUENTA_BLOQUEADA_TEMPORALMENTE" ->
                    ResponseEntity.status(423).body(new AuthResponse("Cuenta bloqueada temporalmente (intenta más tarde)"));
            default -> ResponseEntity.status(401).body(new AuthResponse("Credenciales inválidas"));
        };
    }

    // 🗑️ HU034: Cerrar cuenta
    @PostMapping("/{id}/cerrar")
    public ResponseEntity<AuthResponse> cerrarCuenta(@PathVariable Long id,
                                                     @RequestBody CloseAccountRequest req) {
        if (!req.isConfirm()) {
            return ResponseEntity.badRequest().body(new AuthResponse("Confirmación requerida"));
        }

        boolean ok = service.closeAccount(id, true);
        if (ok)
            return ResponseEntity.ok(new AuthResponse("Cuenta suspendida correctamente"));
        return ResponseEntity.status(404).body(new AuthResponse("Cuenta no encontrada"));
    }

    // 🧾 Registro (solo pruebas o nuevos restaurantes)
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody AuthRequest req) {
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

// HU31 Registro de restaurante
@PostMapping("/registro-completo")
public ResponseEntity<?> registroCompleto(@RequestBody RegisterRequest req) {
    try {
        Restaurante nuevo = new Restaurante();
        nuevo.setNombre(req.getNombre());
        nuevo.setDireccion(req.getDireccion());
        nuevo.setTelefono(req.getTelefono());
        nuevo.setCorreo(req.getCorreo());
        nuevo.setPassword(req.getPassword());
        nuevo.setTipoCocina(req.getTipoCocina());

        Restaurante creado = service.registrarNuevo(nuevo);
        return ResponseEntity.created(URI.create("/api/restaurante/" + creado.getId()))
                .body(new AuthResponse("Registro exitoso, revisa tu correo para confirmar la cuenta"));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body(new AuthResponse("Error al registrar restaurante"));
    }
}
// HU031 Confirmar cuenta
@GetMapping("/confirmar")
public ResponseEntity<?> confirmarCuenta(@RequestParam String correo) {
    boolean activado = service.confirmarCuenta(correo);
    if (activado) {
        return ResponseEntity.ok("Cuenta confirmada correctamente. Ya puede iniciar sesión.");
    } else {
        return ResponseEntity.badRequest().body("Cuenta no encontrada o ya estaba activa.");
    }
}

}


