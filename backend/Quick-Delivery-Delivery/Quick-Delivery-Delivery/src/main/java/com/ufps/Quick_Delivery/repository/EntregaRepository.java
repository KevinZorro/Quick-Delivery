package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.models.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EntregaRepository extends JpaRepository<Entrega, UUID> {

    Optional<Entrega> findByPedidoId(UUID pedidoId);
}
