package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.UsuarioDto;
import com.ufps.Quick_Delivery.model.Usuario;
import com.ufps.Quick_Delivery.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Crear usuario
    @PostMapping("/crear")
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody UsuarioDto dto) {
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
}
