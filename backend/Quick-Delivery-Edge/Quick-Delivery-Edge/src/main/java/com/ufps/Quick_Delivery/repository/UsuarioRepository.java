package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Usuario;  // ✅ ruta correcta de la entidad
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);
}
