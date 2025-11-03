package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.DireccionRequestDto;
import com.ufps.Quick_Delivery.dto.DireccionResponseDto;
import com.ufps.Quick_Delivery.service.DireccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/direcciones")
@RequiredArgsConstructor
public class DireccionController {
    
    private final DireccionService direccionService;
    
    @PostMapping
    public ResponseEntity<DireccionResponseDto> crearDireccion(
            @Valid @RequestBody DireccionRequestDto requestDto,
            Authentication authentication) {
        
        UUID usuarioId = UUID.fromString(authentication.getName());
        DireccionResponseDto direccion = direccionService.crearDireccion(requestDto, usuarioId);
        return new ResponseEntity<>(direccion, HttpStatus.CREATED);
    }
    
    // ⭐ Endpoint para obtener MIS direcciones (usuario autenticado)
    @GetMapping("/mis-direcciones")
    public ResponseEntity<List<DireccionResponseDto>> obtenerMisDirecciones(Authentication authentication) {
        UUID usuarioId = UUID.fromString(authentication.getName());
        List<DireccionResponseDto> direcciones = direccionService.obtenerDireccionesPorUsuario(usuarioId);
        return ResponseEntity.ok(direcciones);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DireccionResponseDto> obtenerDireccionPorId(@PathVariable("id") UUID id) {
        DireccionResponseDto direccion = direccionService.obtenerDireccionPorId(id);
        return ResponseEntity.ok(direccion);
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DireccionResponseDto>> obtenerDireccionesPorUsuario(@PathVariable("usuarioId") UUID usuarioId) {
        List<DireccionResponseDto> direcciones = direccionService.obtenerDireccionesPorUsuario(usuarioId);
        return ResponseEntity.ok(direcciones);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DireccionResponseDto> actualizarDireccion(
            @PathVariable("id") UUID id,
            @Valid @RequestBody DireccionRequestDto requestDto,
            Authentication authentication) {
        
        UUID usuarioId = UUID.fromString(authentication.getName());
        DireccionResponseDto direccion = direccionService.actualizarDireccion(id, requestDto, usuarioId);
        return ResponseEntity.ok(direccion);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDireccion(
            @PathVariable("id") UUID id,
            Authentication authentication) {
        
        UUID usuarioId = UUID.fromString(authentication.getName());
        direccionService.eliminarDireccion(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
