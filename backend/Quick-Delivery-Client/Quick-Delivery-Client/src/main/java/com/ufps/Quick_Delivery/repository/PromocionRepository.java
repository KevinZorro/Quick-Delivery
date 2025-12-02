package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
@Repository
public interface PromocionRepository extends JpaRepository<Promocion, UUID> {
    List<Promocion> findByRestauranteId(UUID restauranteId);

    Optional<Promocion> findByCodigo(String codigo);

}
