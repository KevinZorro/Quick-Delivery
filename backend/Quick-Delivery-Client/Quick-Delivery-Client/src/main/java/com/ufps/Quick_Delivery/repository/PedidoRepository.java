package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.model.EstadoPedido;
import com.ufps.Quick_Delivery.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    Optional<Pedido> findByClienteAndRestauranteIdAndEstado(Cliente cliente, UUID restauranteId, EstadoPedido estado);

    /**
     * Buscar pedidos por usuarioId del cliente (a través de la relación)
     * Spring Data genera automáticamente: WHERE p.cliente.usuarioId = ?
     */
    List<Pedido> findByCliente_UsuarioIdOrderByFechaCreacionDesc(UUID usuarioId);
    
    /**
     * Buscar pedidos por usuarioId y estado
     */
    List<Pedido> findByCliente_UsuarioIdAndEstadoOrderByFechaCreacionDesc(
        UUID usuarioId, 
        EstadoPedido estado
    );
    
    /**
     * Contar pedidos de un usuario
     */
    long countByCliente_UsuarioId(UUID usuarioId);
    
    /**
     * Buscar pedidos por cliente directo
     */
    List<Pedido> findByClienteOrderByFechaCreacionDesc(Cliente cliente);

}
