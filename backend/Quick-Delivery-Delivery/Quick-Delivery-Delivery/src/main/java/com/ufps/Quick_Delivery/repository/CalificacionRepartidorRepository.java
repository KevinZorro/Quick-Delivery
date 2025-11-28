package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.models.CalificacionRepartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CalificacionRepartidorRepository extends JpaRepository<CalificacionRepartidor, UUID> {

    // Obtener todas las opiniones ordenadas por fecha, de un repartidor
    List<CalificacionRepartidor> findByRepartidorIdOrderByFechaCreacionDesc(UUID repartidorId);

    // Verificar si ya existe una calificación para un pedido (para evitar duplicidad)
    boolean existsByPedidoId(UUID pedidoId);
}
