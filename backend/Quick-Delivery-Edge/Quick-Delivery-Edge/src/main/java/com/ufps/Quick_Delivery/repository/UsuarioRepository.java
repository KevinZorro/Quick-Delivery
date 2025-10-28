package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    java.util.Optional<Usuario> findByCorreo(String correo);
}
