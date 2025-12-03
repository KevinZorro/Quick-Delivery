package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.HorarioAtencion;
import com.ufps.Quick_Delivery.model.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HorarioAtencionRepository extends JpaRepository<HorarioAtencion, UUID> {

    List<HorarioAtencion> findByRestauranteId(UUID restauranteId);

    HorarioAtencion findByRestauranteIdAndDiaSemana(UUID restauranteId, DiaSemana diaSemana);
}