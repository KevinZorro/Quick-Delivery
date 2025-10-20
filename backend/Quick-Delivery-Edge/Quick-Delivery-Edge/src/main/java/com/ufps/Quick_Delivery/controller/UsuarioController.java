package com.ufps.Quick_Delivery.controller;


import com.ufps.Quick_Delivery.model.Usuario;
import com.ufps.Quick_Delivery.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body("❌ El correo ya está registrado");
        }
        usuario.setActivo(true);
        usuario.setLastLogin(LocalDateTime.now());
        Usuario nuevo = usuarioRepository.save(usuario);
        return ResponseEntity.ok(nuevo);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario request) {
        return usuarioRepository.findByEmail(request.getEmail())
                .map(usuario -> {
                    if (usuario.getPassword().equals(request.getPassword())) {
                        usuario.setLastLogin(LocalDateTime.now());
                        usuarioRepository.save(usuario);
                        return ResponseEntity.ok("✅ Login exitoso como " + usuario.getRol());
                    } else {
                        return ResponseEntity.status(401).body("❌ Contraseña incorrecta");
                    }
                })
                .orElse(ResponseEntity.status(404).body("❌ Usuario no encontrado"));
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }
}