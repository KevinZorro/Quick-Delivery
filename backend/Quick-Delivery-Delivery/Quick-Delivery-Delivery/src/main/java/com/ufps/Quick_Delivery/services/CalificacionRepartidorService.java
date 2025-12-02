package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.dto.NuevaCalificacionRepartidorDto;
import com.ufps.Quick_Delivery.models.CalificacionRepartidor;
import com.ufps.Quick_Delivery.repository.CalificacionRepartidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalificacionRepartidorService {

    private final CalificacionRepartidorRepository calificacionRepo;

    @Transactional
    public void registrarCalificacion(UUID repartidorId, NuevaCalificacionRepartidorDto dto) {
        if (dto.getCalificacion() < 1 || dto.getCalificacion() > 5)
            throw new IllegalArgumentException("Calificación fuera de rango");

        if (calificacionRepo.existsByPedidoId(dto.getPedidoId()))
            throw new RuntimeException("Ya existe calificación para este pedido");

        CalificacionRepartidor entity = CalificacionRepartidor.builder()
                .repartidorId(repartidorId)
                .pedidoId(dto.getPedidoId())
                .clienteId(dto.getClienteId())
                .calificacion(dto.getCalificacion())
                .comentario(dto.getComentario())
                .fechaCreacion(LocalDateTime.now())
                .build();
        calificacionRepo.save(entity);
    }

    @Transactional(readOnly = true)
    public List<CalificacionRepartidor> obtenerCalificacionesRepartidor(UUID repartidorId) {
        return calificacionRepo.findByRepartidorIdOrderByFechaCreacionDesc(repartidorId);
    }

    @Transactional(readOnly = true)
    public double obtenerPromedioRepartidor(UUID repartidorId) {
        List<CalificacionRepartidor> lista = obtenerCalificacionesRepartidor(repartidorId);
        return lista.stream().mapToInt(CalificacionRepartidor::getCalificacion).average().orElse(0.0);
    }
}
