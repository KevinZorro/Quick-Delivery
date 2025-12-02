// src/main/java/com/ufps/Quick_Delivery/repository/ResenaRestauranteRepository.java
package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.ResenaRestaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ResenaRestauranteRepository extends JpaRepository<ResenaRestaurante, UUID> {
    List<ResenaRestaurante> findByRestauranteIdOrderByFechaCreacionDesc(UUID restauranteId);
    boolean existsByPedidoId(UUID pedidoId);
}
