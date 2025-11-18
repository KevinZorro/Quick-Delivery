package com.ufps.Quick_Delivery.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ufps.Quick_Delivery.dto.LoginRequestDto;
import com.ufps.Quick_Delivery.dto.LoginResponseDto;
import com.ufps.Quick_Delivery.dto.UsuarioDto;
import com.ufps.Quick_Delivery.model.Usuario;
import com.ufps.Quick_Delivery.service.AuthService;
import com.ufps.Quick_Delivery.dto.CambiarContrasenaRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto) {
        return authService.login(dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UsuarioDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        Usuario nuevo = authService.registrar(dto);
        return ResponseEntity.ok(nuevo);
    }

    @GetMapping("/verificar-correo")
    public ResponseEntity<Boolean> verificarCorreo(@RequestParam String correo) {
        boolean existe = authService.verificarCorreo(correo);
        return ResponseEntity.ok(existe);
    }

    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<Map<String, Object>> recuperarContrasena(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        Map<String, Object> response = new HashMap<>();
        if (!authService.verificarCorreo(correo)) {
            response.put("mensaje", "Correo no encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        authService.enviarTokenRecuperacion(correo);
        response.put("mensaje", "Se ha enviado el enlace de recuperación");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> validarToken(@RequestParam("token") String token) {
        boolean valido = authService.validarToken(token);
        Map<String, Object> response = new HashMap<>();
        response.put("valido", valido);
        if (!valido) {
            response.put("mensaje", "Token inválido o expirado");
            return ResponseEntity.badRequest().body(response);
        }
        response.put("mensaje", "Token válido, puede cambiar su contraseña");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> cambiarContrasena(@RequestBody CambiarContrasenaRequest request) {
        Map<String, Object> response = new HashMap<>();
        boolean actualizado = authService.actualizarContrasena(request.getToken(), request.getNuevaContrasena());
        if (!actualizado) {
            response.put("mensaje", "Error al actualizar la contraseña. Token inválido o expirado.");
            return ResponseEntity.badRequest().body(response);
        }
        response.put("mensaje", "Contraseña actualizada correctamente");
        return ResponseEntity.ok(response);
    }
}
