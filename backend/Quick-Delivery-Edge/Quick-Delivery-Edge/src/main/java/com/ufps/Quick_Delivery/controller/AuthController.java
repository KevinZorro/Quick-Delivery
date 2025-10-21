package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.Auth.AuthRequest;
import com.ufps.Quick_Delivery.Auth.AuthResponse;
import com.ufps.Quick_Delivery.model.Usuario;
import com.ufps.Quick_Delivery.repository.UsuarioRepository;
import com.ufps.Quick_Delivery.service.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // URL del frontend
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            if (request.getCorreo() == null || request.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(false, "Correo y contraseña son obligatorios", null, null, null));
            }

            Optional<Usuario> userOpt = usuarioRepository.findByCorreo(request.getCorreo());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(false, "Credenciales inválidas", null, null, null));
            }

            Usuario user = userOpt.get();

            if (!user.isActivo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthResponse(false, "Usuario deshabilitado", null, null, null));
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(false, "Credenciales inválidas", null, null, null));
            }

            String token = jwtService.generateToken(user.getCorreo(), user.getRol().name(), user.getId());

            user.setLastLogin(LocalDateTime.now());
            usuarioRepository.save(user);

            String serviceUrl = getServiceUrlByRole(user.getRol().name());

            return ResponseEntity.ok(new AuthResponse(true, "Login exitoso", token, user.getRol().name(), serviceUrl));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(false, "Error en el servidor: " + e.getMessage(), null, null, null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario user) {
        try {
            if (usuarioRepository.existsByCorreo(user.getCorreo())) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(false, "El correo ya está registrado", null, null, null));
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActivo(true);

            Usuario savedUser = usuarioRepository.save(user);
            String token = jwtService.generateToken(savedUser.getCorreo(), savedUser.getRol().name(), savedUser.getId());
            String serviceUrl = getServiceUrlByRole(savedUser.getRol().name());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse(true, "Usuario registrado exitosamente", token,
                            savedUser.getRol().name(), serviceUrl));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(false, "Error al registrar usuario: " + e.getMessage(), null, null, null));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(false, "Token no proporcionado", null, null, null));
            }

            String token = authHeader.substring(7);
            if (jwtService.validateToken(token)) {
                String correo = jwtService.extractCorreo(token); // si usas extractEmail cámbialo a extractCorreo
                String role = jwtService.extractRole(token);
                return ResponseEntity.ok(new AuthResponse(true, "Token válido", token, role, null));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(false, "Token inválido o expirado", null, null, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Error al validar token", null, null, null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(new AuthResponse(true, "Sesión cerrada exitosamente", null, null, null));
    }

    private String getServiceUrlByRole(String role) {
        switch (role.toUpperCase()) {
            case "CLIENTE":
                return "http://localhost:8080";
            case "RESTAURANTE":
                return "http://localhost:8081";
            case "REPARTIDOR":
                return "http://localhost:8082";
            case "ADMIN":
                return "http://localhost:8084";
            default:
                return null;
        }
    }
}
