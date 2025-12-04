package com.ufps.Quick_Delivery.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufps.Quick_Delivery.model.DiaSemana;
import com.ufps.Quick_Delivery.model.HorarioAtencion;

public interface HorarioAtencionRepository extends JpaRepository<HorarioAtencion, UUID> {

    List<HorarioAtencion> findByRestauranteId(UUID restauranteId);
    

    HorarioAtencion findByRestauranteIdAndDiaSemana(UUID restauranteId, DiaSemana diaSemana);
}