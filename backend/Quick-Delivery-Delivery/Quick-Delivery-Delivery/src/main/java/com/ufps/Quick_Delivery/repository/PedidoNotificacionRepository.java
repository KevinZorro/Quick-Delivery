package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.models.PedidoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PedidoNotificacionRepository extends JpaRepository<PedidoNotificacion, UUID> {
    List<PedidoNotificacion> findByProcesadoFalseOrderByFechaCreacionDesc();
    Optional<PedidoNotificacion> findByPedidoId(UUID pedidoId);
}

