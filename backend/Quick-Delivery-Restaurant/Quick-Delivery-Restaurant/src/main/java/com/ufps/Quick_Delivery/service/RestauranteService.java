package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.RestauranteRequestDto;
import com.ufps.Quick_Delivery.dto.RestauranteResponseDto;
import com.ufps.Quick_Delivery.model.Categoria;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.repository.RestauranteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;

    @Transactional
    public RestauranteResponseDto crear(RestauranteRequestDto requestDto) {
        // Verificar si ya existe un restaurante para este usuario
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

        Restaurante savedRestaurante = restauranteRepository.save(restaurante);
        return mapToResponseDto(savedRestaurante);
    }

    @Transactional(readOnly = true)
    public RestauranteResponseDto obtenerPorId(UUID id) {
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
        return restauranteRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
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
    public RestauranteResponseDto actualizar(UUID id, RestauranteRequestDto requestDto) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado con id: " + id));

        restaurante.setDescripcion(requestDto.getDescripcion());
        restaurante.setCategoria(requestDto.getCategoria());
        restaurante.setImagenUrl(requestDto.getImagenUrl());

        Restaurante updatedRestaurante = restauranteRepository.save(restaurante);
        return mapToResponseDto(updatedRestaurante);
    }

    @Transactional
    public void actualizarCalificacion(UUID id, Double nuevaCalificacion) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado con id: " + id));

        if (nuevaCalificacion < 0 || nuevaCalificacion > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 0 y 5");
        }

        restaurante.setCalificacionPromedio(nuevaCalificacion);
        restauranteRepository.save(restaurante);
    }

    @Transactional
    public void eliminar(UUID id) {
        if (!restauranteRepository.existsById(id)) {
            throw new RuntimeException("Restaurante no encontrado con id: " + id);
        }
        restauranteRepository.deleteById(id);
    }

    private RestauranteResponseDto mapToResponseDto(Restaurante restaurante) {
        return RestauranteResponseDto.builder()
                .id(restaurante.getId())
                .usuarioId(restaurante.getUsuarioId())
                .descripcion(restaurante.getDescripcion())
                .categoria(restaurante.getCategoria())
                .calificacionPromedio(restaurante.getCalificacionPromedio())
                .imagenUrl(restaurante.getImagenUrl())
                .build();
    }
}
