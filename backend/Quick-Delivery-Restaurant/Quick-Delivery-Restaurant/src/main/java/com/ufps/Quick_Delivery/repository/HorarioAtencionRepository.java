package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.HorarioAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Repository
public interface HorarioAtencionRepository extends JpaRepository<HorarioAtencion, UUID> {
    
    List<HorarioAtencion> findByRestauranteId(UUID restauranteId);
    
    List<HorarioAtencion> findByRestauranteIdAndDiaSemana(UUID restauranteId, DayOfWeek diaSemana);
    
    boolean existsByRestauranteIdAndDiaSemana(UUID restauranteId, DayOfWeek diaSemana);
    
    void deleteByRestauranteId(UUID restauranteId);
}
