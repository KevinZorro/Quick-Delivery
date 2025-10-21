package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Pedido;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

}