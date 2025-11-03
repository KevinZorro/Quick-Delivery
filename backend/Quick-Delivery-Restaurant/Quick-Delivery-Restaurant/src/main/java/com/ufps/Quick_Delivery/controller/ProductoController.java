package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.ProductoRequestDTO;
import com.ufps.Quick_Delivery.dto.ProductoResponseDTO;
import com.ufps.Quick_Delivery.dto.ProductoUpdateDTO;
import com.ufps.Quick_Delivery.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    
    private final ProductoService productoService;
    
    // ═══════════════════════════════════════════════════════════
    // ENDPOINTS PARA RESTAURANTES (Requieren autenticación)
    // ═══════════════════════════════════════════════════════════
    
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(
            @Valid @RequestBody ProductoRequestDTO requestDTO,
            Authentication authentication) {
        
        UUID usuarioId = UUID.fromString(authentication.getName());
        requestDTO.setUsuarioId(usuarioId);
        
        ProductoResponseDTO productoCreado = productoService.crearProducto(requestDTO);
        return new ResponseEntity<>(productoCreado, HttpStatus.CREATED);
    }
    
    @GetMapping("/mis-productos")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerMisProductos(Authentication authentication) {
        UUID usuarioId = UUID.fromString(authentication.getName());
        List<ProductoResponseDTO> productos = productoService.obtenerProductosPorUsuario(usuarioId);
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/mis-categorias")
    public ResponseEntity<List<String>> obtenerMisCategorias(Authentication authentication) {
        UUID usuarioId = UUID.fromString(authentication.getName());
        List<String> categorias = productoService.obtenerCategoriasPorUsuario(usuarioId);
        return ResponseEntity.ok(categorias);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ProductoUpdateDTO updateDTO,
            Authentication authentication) {
        
        UUID usuarioId = UUID.fromString(authentication.getName());
        ProductoResponseDTO productoActualizado = productoService.actualizarProducto(id, updateDTO, usuarioId);
        return ResponseEntity.ok(productoActualizado);
    }
    
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<ProductoResponseDTO> cambiarDisponibilidad(
            @PathVariable("id") UUID id,
            @RequestParam("disponible") Boolean disponible,
            Authentication authentication) {
        
        UUID usuarioId = UUID.fromString(authentication.getName());
        ProductoResponseDTO productoActualizado = productoService.cambiarDisponibilidad(id, disponible, usuarioId);
        return ResponseEntity.ok(productoActualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable("id") UUID id,
            Authentication authentication) {
        
        UUID usuarioId = UUID.fromString(authentication.getName());
        productoService.eliminarProducto(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
    
    // ═══════════════════════════════════════════════════════════
    // ENDPOINTS PÚBLICOS PARA CLIENTES (Ver menús de restaurantes)
    // ═══════════════════════════════════════════════════════════
    
    // ⭐ Ver todos los productos de un restaurante (por restauranteId)
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerProductosPorRestaurante(
            @PathVariable("restauranteId") UUID restauranteId) {
        List<ProductoResponseDTO> productos = productoService.obtenerProductosPorRestaurante(restauranteId);
        return ResponseEntity.ok(productos);
    }
    
    // ⭐ Ver solo productos disponibles de un restaurante
    @GetMapping("/restaurante/{restauranteId}/disponibles")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerProductosDisponibles(
            @PathVariable("restauranteId") UUID restauranteId) {
        List<ProductoResponseDTO> productos = productoService.obtenerProductosDisponiblesPorRestaurante(restauranteId);
        return ResponseEntity.ok(productos);
    }
    
    // ⭐ Ver productos de un restaurante por categoría
    @GetMapping("/restaurante/{restauranteId}/categoria/{categoria}")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerProductosPorRestauranteYCategoria(
            @PathVariable("restauranteId") UUID restauranteId,
            @PathVariable String categoria) {
        List<ProductoResponseDTO> productos = productoService.obtenerProductosPorRestauranteYCategoria(restauranteId, categoria);
        return ResponseEntity.ok(productos);
    }
    
    // ⭐ Buscar productos por nombre en un restaurante
    @GetMapping("/restaurante/{restauranteId}/buscar")
    public ResponseEntity<List<ProductoResponseDTO>> buscarProductosPorNombre(
            @PathVariable("restauranteId") UUID restauranteId,
            @RequestParam String nombre) {
        List<ProductoResponseDTO> productos = productoService.buscarProductosPorRestauranteYNombre(restauranteId, nombre);
        return ResponseEntity.ok(productos);
    }
    
    // ⭐ Ver categorías de un restaurante
    @GetMapping("/restaurante/{restauranteId}/categorias")
    public ResponseEntity<List<String>> obtenerCategoriasPorRestaurante(
            @PathVariable("restauranteId") UUID restauranteId) {
        List<String> categorias = productoService.obtenerCategoriasPorRestaurante(restauranteId);
        return ResponseEntity.ok(categorias);
    }
    
    // ⭐ Ver un producto específico por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorId(@PathVariable("id") UUID id) {
        ProductoResponseDTO producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(producto);
    }
    
    // ⭐ Ver todos los productos (útil para admin o búsqueda global)
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodosLosProductos() {
        List<ProductoResponseDTO> productos = productoService.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }
}
