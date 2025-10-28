package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.ReporteRequest;
import com.ufps.Quick_Delivery.dto.RestauranteRequest;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.repository.RestauranteRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RestauranteService {

    private final RestauranteRepository repo;

    public RestauranteService(RestauranteRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Optional<Restaurante> buscarPorId(@NotNull UUID id) {
        return repo.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Restaurante> listarTodos() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Restaurante> listarPorUsuarioId(UUID usuarioId) {
        return repo.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Restaurante crear(Restaurante restaurante) {
        // Validar que el usuario no tenga ya un restaurante con el mismo nombre
        Optional<Restaurante> existente = repo.findByUsuarioIdAndDescripcion(
                restaurante.getUsuarioId(), 
                restaurante.getDescripcion()
        );
        
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un restaurante con esta descripción para este usuario");
        }

        // Validar campos obligatorios
        if (restaurante.getUsuarioId() == null) {
            throw new IllegalArgumentException("Faltan campos obligatorios");
        }

        // Si no tiene imagen, asignar una por defecto
        if (restaurante.getImagenUrl() == null || restaurante.getImagenUrl().isEmpty()) {
            restaurante.setImagenUrl("/assets/images/restaurante-default.jpg");
        }

        return repo.save(restaurante);
    }

    @Transactional
    public Restaurante actualizar(UUID id, RestauranteRequest req) {
        Optional<Restaurante> or = repo.findById(id);
        if (or.isEmpty()) return null;

        Restaurante r = or.get();

        if (req.getDescripcion() != null) {
            r.setDescripcion(req.getDescripcion());
        }
        
        if (req.getCategoria() != null) {
            r.setCategoria(req.getCategoria());
        }
        
        if (req.getCalificacionPromedio() != null) {
            if (req.getCalificacionPromedio() < 0.0 || req.getCalificacionPromedio() > 5.0) {
                throw new IllegalArgumentException("La calificación debe estar entre 0.0 y 5.0");
            }
            r.setCalificacionPromedio(req.getCalificacionPromedio());
        }
        
        if (req.getImagenUrl() != null) {
            r.setImagenUrl(req.getImagenUrl());
        }

        return repo.save(r);
    }

    @Transactional
    public boolean eliminar(UUID id) {
        if (!repo.existsById(id)) {
            return false;
        }
        repo.deleteById(id);
        return true;
    }

    @Transactional
    public boolean actualizarCalificacion(UUID id, Double nuevaCalificacion) {
        Optional<Restaurante> or = repo.findById(id);
        if (or.isEmpty()) return false;

        if (nuevaCalificacion < 0.0 || nuevaCalificacion > 5.0) {
            throw new IllegalArgumentException("La calificación debe estar entre 0.0 y 5.0");
        }

        Restaurante r = or.get();
        r.setCalificacionPromedio(nuevaCalificacion);
        repo.save(r);
        return true;
    }

    public byte[] generarReporte(UUID id, ReporteRequest req) {
        if (req.getFechaInicio() == null || req.getFechaFin() == null || req.getTipoReporte() == null) {
            throw new IllegalArgumentException("Debe especificar fechaInicio, fechaFin y tipoReporte");
        }

        Optional<Restaurante> or = repo.findById(id);
        if (or.isEmpty()) {
            throw new IllegalArgumentException("Restaurante no encontrado");
        }

        Restaurante r = or.get();

        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Desempeño\n");
        sb.append("Restaurante ID: ").append(id).append("\n");
        sb.append("Descripción: ").append(r.getDescripcion()).append("\n");
        sb.append("Categoría: ").append(r.getCategoria().getNombre()).append("\n");
        sb.append("Calificación Promedio: ").append(r.getCalificacionPromedio()).append("\n");
        sb.append("Tipo de Reporte: ").append(req.getTipoReporte()).append("\n");
        sb.append("Rango de Fechas: ").append(req.getFechaInicio()).append(" a ").append(req.getFechaFin()).append("\n\n");
        sb.append("Fecha,Descripción,Métrica,Valor\n");

        for (int i = 1; i <= 5; i++) {
            sb.append(req.getFechaInicio().plusDays(i))
                    .append(",Ejemplo de registro ")
                    .append(i)
                    .append(",")
                    .append(req.getTipoReporte())
                    .append(",")
                    .append((int) (Math.random() * 100))
                    .append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
