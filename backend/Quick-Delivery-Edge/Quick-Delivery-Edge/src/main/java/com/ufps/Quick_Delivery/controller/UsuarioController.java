package com.ufps.Quick_Delivery.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufps.Quick_Delivery.dto.EliminarCuentaRequest;
import com.ufps.Quick_Delivery.dto.UsuarioDto;
import com.ufps.Quick_Delivery.model.Rol;
import com.ufps.Quick_Delivery.model.Usuario;
import com.ufps.Quick_Delivery.repository.UsuarioRepository;
import com.ufps.Quick_Delivery.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Crear usuario
    @PostMapping("/crear")
public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioDto dto) {
    try {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setContraseña(dto.getContraseña());
        usuario.setCorreo(dto.getCorreo());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(dto.getRol());
        usuario.setActivo(true);
        usuario.setFecharegistro(java.time.LocalDateTime.now());

        Usuario guardado = usuarioService.crearUsuario(usuario);
        return ResponseEntity.ok(guardado);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


    // Listar todos
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    // Obtener uno por id
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable("id") UUID id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizar
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable UUID id, @Valid @RequestBody UsuarioDto dto) {
        Usuario datos = new Usuario();
        datos.setNombre(dto.getNombre());
        datos.setContraseña(dto.getContraseña());
        datos.setCorreo(dto.getCorreo());
        datos.setTelefono(dto.getTelefono());
        datos.setRol(dto.getRol());
        return usuarioService.actualizarUsuario(id, datos)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable UUID id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminar mi propia cuenta (con verificación de contraseña)
    @PostMapping("/mi-cuenta")
    public ResponseEntity<?> eliminarMiCuenta(@Valid @RequestBody EliminarCuentaRequest request) {
        try {
            // Extraer userId del SecurityContext (establecido por JwtAuthenticationFilter)
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).body("Usuario no autenticado");
            }

            String userIdStr = authentication.getName();
            UUID userId;
            try {
                userId = UUID.fromString(userIdStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(401).body("Token inválido");
            }

            // Obtener el usuario de la base de datos
            Usuario usuario = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validar la contraseña
            if (!passwordEncoder.matches(request.getContraseña(), usuario.getContraseña())) {
                return ResponseEntity.status(401).body("Contraseña incorrecta");
            }

            // Si la contraseña es correcta, hacer soft delete
            usuarioService.eliminarUsuario(userId);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar la cuenta: " + e.getMessage());
        }
    }

    // Obtener IDs de usuarios activos por rol
    @GetMapping("/activos/rol/{rol}")
    public ResponseEntity<List<UUID>> obtenerUsuariosActivosPorRol(@PathVariable("rol") String rol) {
        try {
            Rol rolEnum = Rol.valueOf(rol.toUpperCase());
            List<Usuario> usuarios = usuarioRepository.findByRolAndActivoTrue(rolEnum);
            List<UUID> userIds = usuarios.stream()
                    .map(Usuario::getId)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userIds);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}