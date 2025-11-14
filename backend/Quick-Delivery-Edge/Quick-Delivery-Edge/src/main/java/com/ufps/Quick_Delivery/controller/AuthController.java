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
    public ResponseEntity<?> recuperarContrasena(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        if (!authService.verificarCorreo(correo)) {
            return ResponseEntity.badRequest().body("Correo no encontrado");
        }
        authService.enviarTokenRecuperacion(correo);
        return ResponseEntity.ok("Se ha enviado el enlace de recuperación");
    }

    @GetMapping("/reset-password")
public ResponseEntity<?> validarToken(@RequestParam("token") String token) {
    boolean valido = authService.validarToken(token);
    if (!valido) {
        return ResponseEntity.badRequest().body("Token inválido o expirado");
    }
    return ResponseEntity.ok("Token válido, puede cambiar su contraseña");
}

@PostMapping("/reset-password")
public ResponseEntity<?> cambiarContrasena(@RequestBody CambiarContrasenaRequest request) {
    boolean actualizado = authService.actualizarContrasena(request.getToken(), request.getNuevaContrasena());
    if (!actualizado) {
        return ResponseEntity.badRequest().body("Error al actualizar la contraseña. Token inválido o expirado.");
    }
    return ResponseEntity.ok("Contraseña actualizada correctamente");
}


}
