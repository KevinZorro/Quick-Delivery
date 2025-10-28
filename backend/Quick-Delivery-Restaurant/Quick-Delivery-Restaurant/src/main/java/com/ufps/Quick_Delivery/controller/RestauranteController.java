package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.AuthRequest;
import com.ufps.Quick_Delivery.dto.AuthResponse;
import com.ufps.Quick_Delivery.dto.CloseAccountRequest;
import com.ufps.Quick_Delivery.dto.RegisterRequest;
import com.ufps.Quick_Delivery.dto.ReporteRequest;
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
    public ResponseEntity<Restaurante> obtenerPedido(@PathVariable("id") UUID id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Restaurante>> listarPedidos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        if (req.getCorreo() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(new AuthResponse("Faltan campos obligatorios"));
        }

        String result = service.attemptLogin(req.getCorreo(), req.getPassword());

        if (result.equals("OK")) {
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

    @PostMapping("/{id}/cerrar")
    public ResponseEntity<AuthResponse> cerrarCuenta(@PathVariable UUID id, @RequestBody CloseAccountRequest req) {
        if (!req.isConfirm()) {
            return ResponseEntity.badRequest().body(new AuthResponse("Confirmación requerida"));
        }

        boolean ok = service.closeAccount(id, true);

        if (ok)
            return ResponseEntity.ok(new AuthResponse("Cuenta suspendida correctamente"));
        return ResponseEntity.status(404).body(new AuthResponse("Cuenta no encontrada"));
    }

    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registro(@RequestBody RegisterRequest req) {
        Restaurante r = service.createIfNotExists(req);
        return ResponseEntity.created(URI.create("/api/restaurante/" + r.getId()))
                .body(new AuthResponse("Registro exitoso"));
    }

// HU31 Registro de restaurante
@PostMapping("/registro-completo")
public ResponseEntity<?> registroCompleto(@RequestBody RegisterRequest req) {
    try {
        // Validación de campos obligatorios
        if (req.getNombre() == null || req.getDireccion() == null || req.getTelefono() == null ||
            req.getCorreo() == null || req.getPassword() == null || req.getTipoCocina() == null) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("Faltan campos obligatorios"));
        }

        Restaurante nuevo = new Restaurante();
        nuevo.setNombre(req.getNombre());
        nuevo.setDireccion(req.getDireccion());
        nuevo.setTelefono(req.getTelefono());
        nuevo.setCorreo(req.getCorreo());
        nuevo.setPassword(req.getPassword());
        nuevo.setTipoCocina(req.getTipoCocina());

        // Guardar documentos legales solo si vienen en la petición
        if (req.getDocumentosLegales() != null) {
            nuevo.setDocumentosLegales(req.getDocumentosLegales());
        }

        Restaurante creado = service.registrarNuevo(nuevo);

        return ResponseEntity.created(URI.create("/api/restaurante/" + creado.getId()))
                .body(new AuthResponse("Registro exitoso,tu cuenta esta activa"));

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage()));
    } catch (Exception e) {
        e.printStackTrace(); // Para depuración en consola
        return ResponseEntity.internalServerError()
                .body(new AuthResponse("Error al registrar restaurante"));
    }
}
// HU031 Confirmar cuenta
//@GetMapping("/confirmar")
//public ResponseEntity<?> confirmarCuenta(@RequestParam String correo) {
//    boolean activado = service.confirmarCuenta(correo);
//    if (activado) {
//        return ResponseEntity.ok("Cuenta confirmada correctamente. Ya puede iniciar sesión.");
//    } else {
//        return ResponseEntity.badRequest().body("Cuenta no encontrada o ya estaba activa.");
//    }
//}
}


