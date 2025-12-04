package com.ufps.Quick_Delivery.repository;


import com.ufps.Quick_Delivery.model.InterrupcionEspecial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InterrupcionEspecialRepository extends JpaRepository<InterrupcionEspecial, UUID> {

    List<InterrupcionEspecial> findByRestauranteIdAndFecha(UUID restauranteId, LocalDate fecha);
}