package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.models.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, UUID> {
    List<Entrega> findByPedidoId(UUID pedidoId);
}
