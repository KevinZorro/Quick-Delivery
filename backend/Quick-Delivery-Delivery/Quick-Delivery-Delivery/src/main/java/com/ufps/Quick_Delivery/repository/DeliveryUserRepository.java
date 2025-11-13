package com.ufps.Quick_Delivery.repository;

<<<<<<<< HEAD:backend/Quick-Delivery-Client/Quick-Delivery-Client/src/main/java/com/ufps/Quick_Delivery/repository/PedidoRepository.java
import com.ufps.Quick_Delivery.model.Pedido;

import java.util.UUID;

========
import com.ufps.Quick_Delivery.models.DeliveryUser;
>>>>>>>> origin/main:backend/Quick-Delivery-Delivery/Quick-Delivery-Delivery/src/main/java/com/ufps/Quick_Delivery/repository/DeliveryUserRepository.java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
<<<<<<<< HEAD:backend/Quick-Delivery-Client/Quick-Delivery-Client/src/main/java/com/ufps/Quick_Delivery/repository/PedidoRepository.java
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

========
public interface DeliveryUserRepository extends JpaRepository<DeliveryUser, UUID> {
>>>>>>>> origin/main:backend/Quick-Delivery-Delivery/Quick-Delivery-Delivery/src/main/java/com/ufps/Quick_Delivery/repository/DeliveryUserRepository.java
}