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
            u.setFotoPerfil(datos.getFotoPerfil());
            return u;
        });
    }

    // Actualizar foto de perfil
    @Transactional
    public Usuario actualizarFotoPerfil(@NonNull UUID id, String fotoPerfil) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        
        // Si fotoPerfil es null o vacío, eliminar la foto
        if (fotoPerfil == null || fotoPerfil.trim().isEmpty()) {
            usuario.setFotoPerfil(null);
        } else {
            // Validar formato de URL de imagen
            if (!esUrlImagenValida(fotoPerfil)) {
                throw new IllegalArgumentException("La URL proporcionada no es una imagen válida. Formatos permitidos: .jpg, .jpeg, .png, .gif, .webp, .svg, base64, o URLs de servicios de imágenes (Google Images, Imgur, Cloudinary, etc.)");
            }
            usuario.setFotoPerfil(fotoPerfil);
        }
        
        return usuarioRepository.save(usuario);
    }

    // Validar que la URL sea una imagen válida
    private boolean esUrlImagenValida(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        // Verificar si es base64
        if (url.startsWith("data:image/")) {
            return true;
        }
        
        String urlLower = url.toLowerCase();
        
        // Verificar extensiones de imagen al final
        if (urlLower.endsWith(".jpg") || 
            urlLower.endsWith(".jpeg") || 
            urlLower.endsWith(".png") || 
            urlLower.endsWith(".gif") || 
            urlLower.endsWith(".webp") ||
            urlLower.endsWith(".svg")) {
            return true;
        }
        
        // Verificar extensiones de imagen con parámetros de consulta
        if (urlLower.contains(".jpg?") ||
            urlLower.contains(".jpeg?") ||
            urlLower.contains(".png?") ||
            urlLower.contains(".gif?") ||
            urlLower.contains(".webp?") ||
            urlLower.contains(".svg?")) {
            return true;
        }
        
        // Aceptar URLs de servicios conocidos de imágenes que no muestran extensiones
        // Google Images (gstatic.com)
        if (urlLower.contains("gstatic.com/images") || 
            urlLower.contains("gstatic.com/image")) {
            return true;
        }
        
        // Otros servicios comunes de imágenes
        if (urlLower.contains("imgur.com") ||
            urlLower.contains("cloudinary.com") ||
            urlLower.contains("unsplash.com") ||
            urlLower.contains("pexels.com") ||
            urlLower.contains("pixabay.com") ||
            urlLower.contains("images.unsplash.com") ||
            urlLower.contains("cdn.") ||
            urlLower.contains("/image/") ||
            urlLower.contains("/images/") ||
            urlLower.contains("/img/")) {
            return true;
        }
        
        // Verificar si la URL tiene un formato válido de HTTP/HTTPS
        if (urlLower.startsWith("http://") || urlLower.startsWith("https://")) {
            // Si es una URL HTTP/HTTPS válida pero no coincide con ningún patrón,
            // permitirla si contiene indicadores de que podría ser una imagen
            // (esto es más permisivo pero evita rechazar URLs válidas)
            return urlLower.contains("image") || 
                   urlLower.contains("img") || 
                   urlLower.contains("photo") ||
                   urlLower.contains("picture");
        }
        
        return false;
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