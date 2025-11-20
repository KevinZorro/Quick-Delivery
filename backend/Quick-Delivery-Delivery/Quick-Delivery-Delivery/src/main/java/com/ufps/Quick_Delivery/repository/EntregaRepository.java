package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.models.Entrega;
import com.ufps.Quick_Delivery.models.EstadoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, UUID> {
    List<Entrega> findByRepartidorIdOrderByFechaCreacionDesc(UUID repartidorId);
    List<Entrega> findByRepartidorIdAndEstadoOrderByFechaCreacionDesc(UUID repartidorId, EstadoEntrega estado);
    Optional<Entrega> findByPedidoId(UUID pedidoId);
}

