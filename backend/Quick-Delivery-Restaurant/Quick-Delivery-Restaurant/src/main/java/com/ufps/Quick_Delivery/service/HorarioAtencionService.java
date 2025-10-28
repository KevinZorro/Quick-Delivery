package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.HorarioAtencionRequest;
import com.ufps.Quick_Delivery.model.HorarioAtencion;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.repository.HorarioAtencionRepository;
import com.ufps.Quick_Delivery.repository.RestauranteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HorarioAtencionService {

    private final HorarioAtencionRepository horarioRepo;
    private final RestauranteRepository restauranteRepo;

    public HorarioAtencionService(HorarioAtencionRepository horarioRepo, 
                                   RestauranteRepository restauranteRepo) {
        this.horarioRepo = horarioRepo;
        this.restauranteRepo = restauranteRepo;
    }

    @Transactional(readOnly = true)
    public List<HorarioAtencion> listarPorRestaurante(UUID restauranteId) {
        return horarioRepo.findByRestauranteId(restauranteId);
    }

    @Transactional(readOnly = true)
    public List<HorarioAtencion> listarPorRestauranteYDia(UUID restauranteId, DayOfWeek dia) {
        return horarioRepo.findByRestauranteIdAndDiaSemana(restauranteId, dia);
    }

    @Transactional(readOnly = true)
    public Optional<HorarioAtencion> buscarPorId(UUID id) {
        return horarioRepo.findById(id);
    }

    @Transactional
    public HorarioAtencion crear(HorarioAtencionRequest req) {
        // Validar que el restaurante existe
        Restaurante restaurante = restauranteRepo.findById(req.getRestauranteId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurante no encontrado"));

        // Validar campos obligatorios
        if (req.getDiaSemana() == null || req.getApertura() == null || req.getCierre() == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }

        // Validar que la hora de apertura sea antes del cierre
        if (req.getApertura().isAfter(req.getCierre()) || req.getApertura().equals(req.getCierre())) {
            throw new IllegalArgumentException("La hora de apertura debe ser anterior a la de cierre");
        }

        // Validar que no exista ya un horario para ese día
        if (horarioRepo.existsByRestauranteIdAndDiaSemana(req.getRestauranteId(), req.getDiaSemana())) {
            throw new IllegalArgumentException("Ya existe un horario para este día");
        }

        HorarioAtencion horario = HorarioAtencion.builder()
                .restaurante(restaurante)
                .diaSemana(req.getDiaSemana())
                .apertura(req.getApertura())
                .cierre(req.getCierre())
                .build();

        return horarioRepo.save(horario);
    }

    @Transactional
    public HorarioAtencion actualizar(UUID id, HorarioAtencionRequest req) {
        HorarioAtencion horario = horarioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));

        if (req.getDiaSemana() != null) {
            // Validar que no exista otro horario para ese día
            if (!horario.getDiaSemana().equals(req.getDiaSemana()) &&
                horarioRepo.existsByRestauranteIdAndDiaSemana(
                    horario.getRestaurante().getId(), req.getDiaSemana())) {
                throw new IllegalArgumentException("Ya existe un horario para este día");
            }
            horario.setDiaSemana(req.getDiaSemana());
        }

        if (req.getApertura() != null) {
            horario.setApertura(req.getApertura());
        }

        if (req.getCierre() != null) {
            horario.setCierre(req.getCierre());
        }

        // Validar coherencia de horarios
        if (horario.getApertura().isAfter(horario.getCierre()) || 
            horario.getApertura().equals(horario.getCierre())) {
            throw new IllegalArgumentException("La hora de apertura debe ser anterior a la de cierre");
        }

        return horarioRepo.save(horario);
    }

    @Transactional
    public boolean eliminar(UUID id) {
        if (!horarioRepo.existsById(id)) {
            return false;
        }
        horarioRepo.deleteById(id);
        return true;
    }

    @Transactional
    public void eliminarPorRestaurante(UUID restauranteId) {
        horarioRepo.deleteByRestauranteId(restauranteId);
    }
}
