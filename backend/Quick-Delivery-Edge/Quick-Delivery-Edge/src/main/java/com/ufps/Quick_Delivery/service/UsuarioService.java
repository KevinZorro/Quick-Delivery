package com.ufps.Quick_Delivery.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ufps.Quick_Delivery.model.Usuario;
import com.ufps.Quick_Delivery.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Crear usuario
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        return usuarioRepository.save(usuario);
    }

    // Listar todos los usuarios
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    // Buscar usuario por ID
    public Optional<Usuario> obtenerPorId(@NonNull UUID id) {
        return usuarioRepository.findById(id);
    }

    // Actualizar usuario
    @Transactional
    public Optional<Usuario> actualizarUsuario(@NonNull UUID id, Usuario datos) {
        return usuarioRepository.findById(id).map(u -> {
            u.setNombre(datos.getNombre());
            u.setContraseña(datos.getContraseña());
            u.setCorreo(datos.getCorreo());
            u.setTelefono(datos.getTelefono());
            u.setRol(datos.getRol());
            return u;
        });
    }

    // Eliminar usuario (soft delete)
    @Transactional
    public void eliminarUsuario(@NonNull UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    // Reactivar usuario (opcional para futuro)
    @Transactional
    public void reactivarUsuario(@NonNull UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }
}