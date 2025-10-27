package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RestauranteRepository extends JpaRepository<Restaurante, UUID> {
    Optional<Restaurante> findByCorreo(String correo);
}

