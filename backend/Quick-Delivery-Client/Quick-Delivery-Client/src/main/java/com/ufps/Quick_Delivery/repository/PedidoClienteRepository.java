package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.PedidoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoClienteRepository extends JpaRepository<PedidoCliente, Long> {

}