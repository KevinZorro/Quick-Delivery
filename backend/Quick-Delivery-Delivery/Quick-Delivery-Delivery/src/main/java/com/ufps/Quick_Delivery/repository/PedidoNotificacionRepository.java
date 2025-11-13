package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.models.EstadoNotificacion;
import com.ufps.Quick_Delivery.models.PedidoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PedidoNotificacionRepository extends JpaRepository<PedidoNotificacion, UUID> {
    
    // Buscar notificaciones pendientes
    List<PedidoNotificacion> findByEstado(EstadoNotificacion estado);
    
    // Buscar notificación por ID de pedido
    Optional<PedidoNotificacion> findByPedidoId(UUID pedidoId);
    
    // Buscar notificaciones aceptadas por un repartidor
    List<PedidoNotificacion> findByRepartidorIdAndEstado(UUID repartidorId, EstadoNotificacion estado);
    
    // Verificar si un pedido ya tiene una notificación
    boolean existsByPedidoId(UUID pedidoId);
}

