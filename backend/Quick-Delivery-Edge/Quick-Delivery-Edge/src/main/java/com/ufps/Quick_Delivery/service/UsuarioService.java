package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.model.Usuario;
import com.ufps.Quick_Delivery.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Crear usuario
    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Listar todos los usuarios
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    // Buscar usuario por ID
    public Optional<Usuario> obtenerPorId(UUID id) {
        return usuarioRepository.findById(id);
    }

    // Actualizar usuario
    @Transactional
    public Optional<Usuario> actualizarUsuario(UUID id, Usuario datos) {
        return usuarioRepository.findById(id).map(u -> {
            u.setNombre(datos.getNombre());
            u.setContraseña(datos.getContraseña());
            u.setCorreo(datos.getCorreo());
            u.setTelefono(datos.getTelefono());
            u.setRol(datos.getRol());
            // activo y fecharegistro como consideres necesario
            return u;
        });
    }

    // Eliminar usuario
    public void eliminarUsuario(UUID id) {
        usuarioRepository.deleteById(id);
    }
}
