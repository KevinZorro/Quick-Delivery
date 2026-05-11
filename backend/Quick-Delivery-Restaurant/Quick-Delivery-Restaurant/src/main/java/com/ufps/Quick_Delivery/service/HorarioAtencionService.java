package com.ufps.Quick_Delivery.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufps.Quick_Delivery.dto.HorarioAtencionDto;
import com.ufps.Quick_Delivery.dto.InterrupcionEspecialDto;
import com.ufps.Quick_Delivery.model.DiaSemana;
import com.ufps.Quick_Delivery.model.HorarioAtencion;
import com.ufps.Quick_Delivery.model.InterrupcionEspecial;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.repository.HorarioAtencionRepository;
import com.ufps.Quick_Delivery.repository.InterrupcionEspecialRepository;
import com.ufps.Quick_Delivery.repository.RestauranteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HorarioAtencionService {

    private static final ZoneId ZONA_HORARIA_NEGOCIO = ZoneId.of("America/Bogota");

    private final RestauranteRepository restauranteRepository;
    private final HorarioAtencionRepository horarioRepo;
    private final InterrupcionEspecialRepository interrupcionRepo;
    private final HorarioAtencionRepository horarioAtencionRepository;
    


    // CRUD Horarios
        public HorarioAtencion guardarHorario(HorarioAtencionDto dto) {

        System.out.println("========== GUARDAR HORARIO (SERVICE) ==========");
        System.out.println("➡ RestauranteId recibido en DTO: " + dto.getRestauranteId());
        System.out.println("➡ Dia: " + dto.getDiaSemana());
        System.out.println("➡ Apertura: " + dto.getHoraApertura());
        System.out.println("➡ Cierre: " + dto.getHoraCierre());

        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> {
                    System.out.println("❌ Restaurante NO encontrado con ID: " + dto.getRestauranteId());
                    return new RuntimeException("Restaurante no encontrado");
                });

        HorarioAtencion horario = HorarioAtencion.builder()
                .diaSemana(dto.getDiaSemana())
                .horaApertura(dto.getHoraApertura())
                .horaCierre(dto.getHoraCierre())
                .restaurante(restaurante)
                .build();

        HorarioAtencion guardado = horarioRepo.save(horario);

        System.out.println("✔ Horario guardado con ID: " + guardado.getId());
        System.out.println("==============================================");

        return guardado;
    }


    public List<HorarioAtencion> listarHorarios(UUID restauranteId) {
        return horarioRepo.findByRestauranteId(restauranteId);
    }

    public Optional<HorarioAtencion> obtenerHorario(UUID id) {
        return horarioRepo.findById(id);
    }

    public void eliminarHorario(UUID id) {
        horarioRepo.deleteById(id);
    }

    // CRUD Interrupciones
    public InterrupcionEspecial guardarInterrupcion(InterrupcionEspecialDto dto) {
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        InterrupcionEspecial interrupcion = InterrupcionEspecial.builder()
                .id(dto.getId())
                .fecha(dto.getFecha())
                .motivo(dto.getMotivo())
                .restaurante(restaurante)
                .build();

        return interrupcionRepo.save(interrupcion);
    }

    public List<InterrupcionEspecial> listarInterrupciones(UUID restauranteId) {
        return interrupcionRepo.findAll()
                .stream()
                .filter(i -> i.getRestaurante() != null && i.getRestaurante().getId().equals(restauranteId))
                .collect(Collectors.toList());
    }

    public void eliminarInterrupcion(UUID id) {
        interrupcionRepo.deleteById(id);
    }

    // Disponibilidad
    public boolean estaDisponible(UUID restauranteId) {
        LocalDate fechaActual = LocalDate.now(ZONA_HORARIA_NEGOCIO);
        LocalTime horaActual = LocalTime.now(ZONA_HORARIA_NEGOCIO);

        // 1. Interrupciones para hoy
        boolean tieneInterrupcion = !interrupcionRepo
                .findByRestauranteIdAndFecha(restauranteId, fechaActual)
                .isEmpty();

        if (tieneInterrupcion) return false;

        // 2. Horario del dia
        List<HorarioAtencion> horarios = horarioRepo.findByRestauranteId(restauranteId);
        if (horarios.isEmpty()) return false;

        DiaSemana diaActual = convertirDiaInglesAEspanol(fechaActual.getDayOfWeek());
        DiaSemana diaAnterior = convertirDiaInglesAEspanol(fechaActual.minusDays(1).getDayOfWeek());

        return horarios.stream()
                .anyMatch(horario -> estaDentroDelHorario(horario, diaActual, diaAnterior, horaActual));
    }

    private boolean estaDentroDelHorario(
            HorarioAtencion horario,
            DiaSemana diaActual,
            DiaSemana diaAnterior,
            LocalTime horaActual
    ) {
        if (horario.getDiaSemana() == null
                || horario.getHoraApertura() == null
                || horario.getHoraCierre() == null) {
            return false;
        }

        LocalTime apertura = horario.getHoraApertura();
        LocalTime cierre = horario.getHoraCierre();

        if (apertura.equals(cierre)) {
            return true;
        }

        boolean horarioCruzaMedianoche = cierre.isBefore(apertura);
        boolean coincideHoy = esMismoDia(horario.getDiaSemana(), diaActual);

        if (!horarioCruzaMedianoche) {
            return coincideHoy && !horaActual.isBefore(apertura) && horaActual.isBefore(cierre);
        }

        boolean coincideAyer = esMismoDia(horario.getDiaSemana(), diaAnterior);
        return (coincideHoy && !horaActual.isBefore(apertura))
                || (coincideAyer && horaActual.isBefore(cierre));
    }

    private boolean esMismoDia(DiaSemana guardado, DiaSemana diaEspanol) {
        return convertirDiaAEspanol(guardado) == diaEspanol;
    }

    private DiaSemana convertirDiaAEspanol(DiaSemana dia) {
        return switch (dia) {
            case MONDAY, LUNES -> DiaSemana.LUNES;
            case TUESDAY, MARTES -> DiaSemana.MARTES;
            case WEDNESDAY, MIERCOLES -> DiaSemana.MIERCOLES;
            case THURSDAY, JUEVES -> DiaSemana.JUEVES;
            case FRIDAY, VIERNES -> DiaSemana.VIERNES;
            case SATURDAY, SABADO -> DiaSemana.SABADO;
            case SUNDAY, DOMINGO -> DiaSemana.DOMINGO;
        };
    }

    private DiaSemana convertirDiaInglesAEspanol(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }

    // Listar todos los restaurantes con disponibilidad calculada
    public List<Map<String, Object>> listarRestaurantesConDisponibilidad() {
        List<Restaurante> all = restauranteRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Restaurante r : all) {
            boolean disponible = estaDisponible(r.getId());
            Map<String, Object> m = new HashMap<>();
            m.put("id", r.getId());
            m.put("usuarioId", r.getUsuarioId());
            m.put("descripcion", r.getDescripcion());
            m.put("categoria", r.getCategoria() != null ? r.getCategoria().name() : null);
            m.put("calificacionPromedio", r.getCalificacionPromedio());
            m.put("imagenUrl", r.getImagenUrl());
            m.put("disponible", disponible);
            result.add(m);
        }
        return result;
    }

    

    @Transactional
    public HorarioAtencion actualizar(UUID id, HorarioAtencionDto dto) {

        HorarioAtencion horario = horarioAtencionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        horario.setDiaSemana(dto.getDiaSemana());
        horario.setHoraApertura(dto.getHoraApertura());
        horario.setHoraCierre(dto.getHoraCierre());
        horario.setRestaurante(restaurante);

        return horarioAtencionRepository.save(horario);
    }

}
