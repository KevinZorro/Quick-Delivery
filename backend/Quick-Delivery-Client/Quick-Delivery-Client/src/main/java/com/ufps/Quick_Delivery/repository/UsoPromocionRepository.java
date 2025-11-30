package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.UsoPromocion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UsoPromocionRepository extends JpaRepository<UsoPromocion, UUID> {
    boolean existsByPromocionIdAndClienteId(UUID promocionId, UUID clienteId);
}
