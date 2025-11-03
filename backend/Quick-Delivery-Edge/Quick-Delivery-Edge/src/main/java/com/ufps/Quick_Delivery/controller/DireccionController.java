package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.DireccionRequestDto;
import com.ufps.Quick_Delivery.dto.DireccionResponseDto;
import com.ufps.Quick_Delivery.model.TipoReferencia;
import com.ufps.Quick_Delivery.service.DireccionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/direcciones")
@RequiredArgsConstructor
public class DireccionController {

    private final DireccionService direccionService;

    @PostMapping
    public ResponseEntity<DireccionResponseDto> crearDireccion(@Valid @RequestBody DireccionRequestDto requestDto) {
        DireccionResponseDto responseDto = direccionService.crearDireccion(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DireccionResponseDto> obtenerDireccionPorId(@PathVariable("id") UUID id) {  // ← AGREGAR "id"
        DireccionResponseDto responseDto = direccionService.obtenerDireccionPorId(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<DireccionResponseDto>> obtenerTodasLasDirecciones() {
        List<DireccionResponseDto> direcciones = direccionService.obtenerTodasLasDirecciones();
        return ResponseEntity.ok(direcciones);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DireccionResponseDto>> obtenerDireccionesPorUsuario(@PathVariable("usuarioId") UUID usuarioId) {  // ← AGREGAR "usuarioId"
        List<DireccionResponseDto> direcciones = direccionService.obtenerDireccionesPorUsuario(usuarioId);
        return ResponseEntity.ok(direcciones);
    }

    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<DireccionResponseDto>> obtenerDireccionesPorCiudad(@PathVariable("ciudad") String ciudad) {  // ← AGREGAR "ciudad"
        List<DireccionResponseDto> direcciones = direccionService.obtenerDireccionesPorCiudad(ciudad);
        return ResponseEntity.ok(direcciones);
    }

    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<List<DireccionResponseDto>> obtenerDireccionesPorUsuarioYTipo(
            @PathVariable("usuarioId") UUID usuarioId,  // ← AGREGAR "usuarioId"
            @PathVariable("tipo") TipoReferencia tipo) {  // ← AGREGAR "tipo"
        List<DireccionResponseDto> direcciones = direccionService.obtenerDireccionesPorUsuarioYTipo(usuarioId, tipo);
        return ResponseEntity.ok(direcciones);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DireccionResponseDto> actualizarDireccion(
            @PathVariable("id") UUID id,  // ← AGREGAR "id"
            @Valid @RequestBody DireccionRequestDto requestDto) {
        DireccionResponseDto responseDto = direccionService.actualizarDireccion(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable("id") UUID id) {  // ← AGREGAR "id"
        direccionService.eliminarDireccion(id);
        return ResponseEntity.noContent().build();
    }
}