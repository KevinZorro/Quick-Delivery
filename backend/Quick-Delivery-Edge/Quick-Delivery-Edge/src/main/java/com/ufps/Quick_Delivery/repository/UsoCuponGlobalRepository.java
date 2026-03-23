package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.UsoCuponGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsoCuponGlobalRepository extends JpaRepository<UsoCuponGlobal, UUID> {
    boolean existsByCuponIdAndClienteId(UUID cuponId, UUID clienteId);
    long countByCuponId(UUID cuponId);
}
