package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.models.DeliveryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryUserRepository extends JpaRepository<DeliveryUser, UUID> {
    Optional<DeliveryUser> findByCorreo(String correo);
    Boolean existsByCorreo(String correo);
    Optional<DeliveryUser> findByCorreoAndActivoTrue(String correo);
}