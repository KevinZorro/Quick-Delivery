package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, UUID> {
    List<ItemPedido> findByPedidoId(UUID pedidoId);
    Optional<ItemPedido> findByPedidoIdAndProductoId(UUID pedidoId, UUID productoId);

}
