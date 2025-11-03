package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.RestauranteRequestDto;
import com.ufps.Quick_Delivery.dto.RestauranteResponseDto;
import com.ufps.Quick_Delivery.model.Categoria;
import com.ufps.Quick_Delivery.service.RestauranteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurante")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteService restauranteService;

    @PostMapping
    public ResponseEntity<RestauranteResponseDto> crear(@Valid @RequestBody RestauranteRequestDto requestDto) {
        RestauranteResponseDto responseDto = restauranteService.crear(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDto> obtenerPorId(@PathVariable("id") UUID id) {
        RestauranteResponseDto responseDto = restauranteService.obtenerPorId(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<RestauranteResponseDto> obtenerPorUsuarioId(@PathVariable("usuarioId") UUID usuarioId) {
        RestauranteResponseDto responseDto = restauranteService.obtenerPorUsuarioId(usuarioId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<RestauranteResponseDto>> listarTodos() {
        List<RestauranteResponseDto> restaurantes = restauranteService.listarTodos();
        return ResponseEntity.ok(restaurantes);
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<RestauranteResponseDto>> listarPorCategoria(@PathVariable("categoria") Categoria categoria) {
        List<RestauranteResponseDto> restaurantes = restauranteService.listarPorCategoria(categoria);
        return ResponseEntity.ok(restaurantes);
    }

    @GetMapping("/calificacion/{minima}")
    public ResponseEntity<List<RestauranteResponseDto>> listarPorCalificacionMinima(@PathVariable("minima") Double minima) {
        List<RestauranteResponseDto> restaurantes = restauranteService.listarPorCalificacionMinima(minima);
        return ResponseEntity.ok(restaurantes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDto> actualizar(
            @PathVariable("id") UUID id,
            @Valid @RequestBody RestauranteRequestDto requestDto) {
        RestauranteResponseDto responseDto = restauranteService.actualizar(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}/calificacion")
    public ResponseEntity<Void> actualizarCalificacion(
            @PathVariable("id") UUID id,
            @RequestParam("calificacion") Double calificacion) {
        restauranteService.actualizarCalificacion(id, calificacion);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") UUID id) {
        restauranteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
