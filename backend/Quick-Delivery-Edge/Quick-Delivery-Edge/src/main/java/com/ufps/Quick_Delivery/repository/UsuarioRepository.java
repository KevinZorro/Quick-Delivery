package com.ufps.Quick_Delivery.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufps.Quick_Delivery.model.Rol;
import com.ufps.Quick_Delivery.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    
    // Métodos para consultar usuarios activos
    Optional<Usuario> findByCorreoAndActivoTrue(String correo);
    List<Usuario> findByActivoTrue();
    List<Usuario> findByRolAndActivoTrue(Rol rol);
}
