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

// 📊 HU07 Generar reportes 
@PostMapping("/{id}/reporte")
public ResponseEntity<?> generarReporte(@PathVariable Long id, @RequestBody ReporteRequest req) {
    try {
        byte[] archivo = service.generarReporte(id, req);

        String tipoArchivo = req.getTipoReporte().equalsIgnoreCase("excel")
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "application/pdf";

        String nombreArchivo = "reporte_" + req.getTipoReporte() + "_" + System.currentTimeMillis() +
                (tipoArchivo.contains("pdf") ? ".pdf" : ".xlsx");

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + nombreArchivo)
                .header("Content-Type", tipoArchivo)
                .body(archivo);

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("Error al generar el reporte");
    }
}
}


