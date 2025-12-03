package com.ufps.Quick_Delivery.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufps.Quick_Delivery.client.PedidoFeignClient;
import com.ufps.Quick_Delivery.config.UsuarioClient;
import com.ufps.Quick_Delivery.dto.PedidoDto;
import com.ufps.Quick_Delivery.dto.RestauranteRequestDto;
import com.ufps.Quick_Delivery.dto.RestauranteResponseDto;
import com.ufps.Quick_Delivery.model.Categoria;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.repository.RestauranteRepository;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Data
@Builder
@Service
@RequiredArgsConstructor
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final PedidoFeignClient pedidoFeignClient;
    private final UsuarioClient usuarioClient;
    private final HorarioAtencionService horarioService;


    @Transactional
    public RestauranteResponseDto crear(@NonNull RestauranteRequestDto requestDto) {

        if (restauranteRepository.existsByUsuarioId(requestDto.getUsuarioId())) {
            throw new RuntimeException("Ya existe un restaurante para este usuario");
        }

        Restaurante restaurante = Restaurante.builder()
                .usuarioId(requestDto.getUsuarioId())
                .descripcion(requestDto.getDescripcion())
                .categoria(requestDto.getCategoria())
                .imagenUrl(requestDto.getImagenUrl())
                .calificacionPromedio(0.0)
                .build();

        Restaurante saved = restauranteRepository.save(restaurante);
        return mapToResponseDto(saved);
    }


    @Transactional(readOnly = true)
    public RestauranteResponseDto obtenerPorId(@NonNull UUID id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado con id: " + id));
        return mapToResponseDto(restaurante);
    }

    @Transactional(readOnly = true)
    public RestauranteResponseDto obtenerPorUsuarioId(UUID usuarioId) {
        Restaurante restaurante = restauranteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No se encontró un restaurante para este usuario"));
        return mapToResponseDto(restaurante);
    }

    @Transactional(readOnly = true)
    public List<RestauranteResponseDto> listarTodos() {
        try {
            List<UUID> usuariosActivosIds = usuarioClient.obtenerUsuariosActivosPorRol("RESTAURANTE");

            return restauranteRepository.findAll().stream()
                    .filter(r -> usuariosActivosIds.contains(r.getUsuarioId()))
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error al obtener usuarios activos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public List<RestauranteResponseDto> listarPorCategoria(Categoria categoria) {
        return restauranteRepository.findByCategoria(categoria).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RestauranteResponseDto> listarPorCalificacionMinima(Double calificacion) {
        return restauranteRepository.findByCalificacionPromedioGreaterThanEqual(calificacion).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RestauranteResponseDto actualizar(@NonNull UUID id, RestauranteRequestDto requestDto) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado con id: " + id));

        restaurante.setDescripcion(requestDto.getDescripcion());
        restaurante.setCategoria(requestDto.getCategoria());
        restaurante.setImagenUrl(requestDto.getImagenUrl());

        Restaurante updated = restauranteRepository.save(restaurante);
        return mapToResponseDto(updated);
    }

    @Transactional
    public void actualizarCalificacion(@NonNull UUID id, Double nuevaCalificacion) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado con id: " + id));

        if (nuevaCalificacion < 0 || nuevaCalificacion > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 0 y 5");
        }

        restaurante.setCalificacionPromedio(nuevaCalificacion);
        restauranteRepository.save(restaurante);
    }

    @Transactional
    public void eliminar(@NonNull UUID id) {
        if (!restauranteRepository.existsById(id)) {
            throw new RuntimeException("Restaurante no encontrado con id: " + id);
        }
        restauranteRepository.deleteById(id);
    }


    // ⭐ versión correcta con DISPONIBILIDAD
    private RestauranteResponseDto mapToResponseDto(Restaurante restaurante) {

        boolean disponible = horarioService.estaDisponible(restaurante.getId());

        return RestauranteResponseDto.builder()
                .id(restaurante.getId())
                .usuarioId(restaurante.getUsuarioId())
                .descripcion(restaurante.getDescripcion())
                .categoria(restaurante.getCategoria())
                .calificacionPromedio(restaurante.getCalificacionPromedio())
                .imagenUrl(restaurante.getImagenUrl())
                .disponible(disponible)
                .build();
    }

    public List<PedidoDto> listarHistorialCompleto(UUID restauranteId) {
        return pedidoFeignClient.obtenerHistorialCompleto(restauranteId);
    }
}
