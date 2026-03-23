package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.CuponGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CuponGlobalRepository extends JpaRepository<CuponGlobal, UUID> {
    List<CuponGlobal> findByActivoTrue();
}
