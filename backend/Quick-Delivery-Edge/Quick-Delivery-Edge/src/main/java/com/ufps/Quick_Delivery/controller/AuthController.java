package com.ufps.Quick_Delivery.controller;


import com.ufps.Quick_Delivery.dto.AuthRequest;
import com.ufps.Quick_Delivery.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        if (req.getCorreo() == null || req.getPassword() == null || req.getRol() == null) {
            return ResponseEntity.badRequest().body(new AuthResponse("Faltan campos obligatorios"));
        }

        switch (req.getRol().toUpperCase()) {
            case "RESTAURANTE":
                // TODO: llamada al servicio de Restaurante
                return ResponseEntity.ok(new AuthResponse("Login restaurante correcto"));
            case "CLIENTE":
                // TODO: llamada al servicio de Cliente
                return ResponseEntity.ok(new AuthResponse("Login cliente correcto"));
            case "REPARTIDOR":
                // TODO: llamada al servicio de Delivery
                return ResponseEntity.ok(new AuthResponse("Login repartidor correcto"));
            default:
                return ResponseEntity.badRequest().body(new AuthResponse("Rol no válido"));
        }
    }
}