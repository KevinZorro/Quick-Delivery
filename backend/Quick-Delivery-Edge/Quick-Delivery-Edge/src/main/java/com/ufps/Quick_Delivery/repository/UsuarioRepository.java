package com.ufps.Quick_Delivery.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufps.Quick_Delivery.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    java.util.Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
}
