package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.model.EstadoPedido;
import com.ufps.Quick_Delivery.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    Optional<Pedido> findByClienteAndRestauranteIdAndEstado(
            Cliente cliente, UUID restauranteId, EstadoPedido estado
    );

    List<Pedido> findByCliente_UsuarioIdOrderByFechaCreacionDesc(UUID usuarioId);

    List<Pedido> findByCliente_UsuarioIdAndEstadoOrderByFechaCreacionDesc(
            UUID usuarioId,
            EstadoPedido estado
    );

    long countByCliente_UsuarioId(UUID usuarioId);

    List<Pedido> findByClienteOrderByFechaCreacionDesc(Cliente cliente);

    // ⭐ ESTE ES EL ÚNICO MÉTODO QUE VA AQUÍ CON LA QUERY
@Query("""
SELECT p FROM Pedido p
WHERE (COALESCE(:restId, p.restauranteId) = p.restauranteId)
  AND (COALESCE(:inicio, p.fechaCreacion) <= p.fechaCreacion)
  AND (COALESCE(:fin, p.fechaCreacion) >= p.fechaCreacion)
  AND (COALESCE(:estado, p.estado) = p.estado)
  AND (COALESCE(:clienteId, p.cliente.id) = p.cliente.id)
ORDER BY p.fechaCreacion DESC
""")
List<Pedido> filtrarPedidos(
        @Param("restId") UUID restauranteId,
        @Param("inicio") LocalDateTime fechaInicio,
        @Param("fin") LocalDateTime fechaFin,
        @Param("estado") EstadoPedido estado,
        @Param("clienteId") UUID clienteId
);


}
