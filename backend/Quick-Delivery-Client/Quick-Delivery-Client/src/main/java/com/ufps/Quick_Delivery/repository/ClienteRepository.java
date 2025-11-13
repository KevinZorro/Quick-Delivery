package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    Optional<Cliente> findByUsuarioId(UUID usuarioId);
}
