// src/main/java/com/ufps/Quick_Delivery/service/ResenaRestauranteService.java
package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.NuevaResenaRestauranteDto;
import com.ufps.Quick_Delivery.model.ResenaRestaurante;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.repository.ResenaRestauranteRepository;
import com.ufps.Quick_Delivery.repository.RestauranteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResenaRestauranteService {

    private final ResenaRestauranteRepository resenaRepo;
    private final RestauranteRepository restauranteRepo;

    @Transactional
    public void registrarResena(NuevaResenaRestauranteDto dto) {
        if (dto.getCalificacion() < 1 || dto.getCalificacion() > 5) {
            throw new IllegalArgumentException("Calificación debe ser de 1 a 5 estrellas");
        }
        if (resenaRepo.existsByPedidoId(dto.getPedidoId())) {
            throw new RuntimeException("Ya existe reseña para este pedido");
        }

        ResenaRestaurante resena = ResenaRestaurante.builder()
                .restauranteId(dto.getRestauranteId())
                .clienteId(dto.getClienteId())
                .pedidoId(dto.getPedidoId())
                .calificacion(dto.getCalificacion())
                .comentario(dto.getComentario())
                .fechaCreacion(LocalDateTime.now())
                .build();
        resenaRepo.save(resena);

        // Actualizar promedio
        List<ResenaRestaurante> todas = resenaRepo.findByRestauranteIdOrderByFechaCreacionDesc(dto.getRestauranteId());
        double promedio = todas.stream().mapToInt(ResenaRestaurante::getCalificacion).average().orElse(0.0);

        Restaurante restaurante = restauranteRepo.findById(dto.getRestauranteId())
                .orElseThrow(() -> new RuntimeException("Restaurante no existe"));
        restaurante.setCalificacionPromedio(promedio);
        restauranteRepo.save(restaurante);
    }

    @Transactional(readOnly = true)
    public List<ResenaRestaurante> listarOpinionesRestaurante(UUID restauranteId) {
        return resenaRepo.findByRestauranteIdOrderByFechaCreacionDesc(restauranteId);
    }
}
