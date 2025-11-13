package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.ProductoRequestDTO;
import com.ufps.Quick_Delivery.dto.ProductoResponseDTO;
import com.ufps.Quick_Delivery.dto.ProductoUpdateDTO;
import com.ufps.Quick_Delivery.model.Producto;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.repository.ProductoRepository;
import com.ufps.Quick_Delivery.repository.RestauranteRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    private final RestauranteRepository restauranteRepository;
    
    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO requestDTO) {
        Restaurante restaurante = restauranteRepository.findByUsuarioId(requestDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado para el usuario con ID: " + requestDTO.getUsuarioId()));
        
        Producto producto = new Producto();
        producto.setRestaurante(restaurante);
        producto.setNombre(requestDTO.getNombre());
        producto.setDescripcion(requestDTO.getDescripcion());
        producto.setPrecio(requestDTO.getPrecio());
        producto.setCategoria(requestDTO.getCategoria());
        producto.setDisponible(requestDTO.getDisponible());
        producto.setImagenUrl(requestDTO.getImagenUrl());
        
        Producto productoGuardado = productoRepository.save(producto);
        return convertirAResponseDTO(productoGuardado);
    }
    
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerProductoPorId(@NonNull UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return convertirAResponseDTO(producto);
    }
    
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerTodosLosProductos() {
        return productoRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }
    
    // ⭐ Para el restaurante autenticado (usa usuarioId)
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosPorUsuario(UUID usuarioId) {
        return productoRepository.findByRestauranteUsuarioId(usuarioId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosDisponiblesPorUsuario(UUID usuarioId) {
        return productoRepository.findByRestauranteUsuarioIdAndDisponible(usuarioId, true).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }
    
    // ⭐ NUEVO: Para clientes que quieren ver el menú (usa restauranteId)
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosPorRestaurante(UUID restauranteId) {
        return productoRepository.findByRestauranteId(restauranteId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }
    
    // ⭐ NUEVO: Para clientes - solo productos disponibles
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosDisponiblesPorRestaurante(UUID restauranteId) {
        return productoRepository.findByRestauranteIdAndDisponible(restauranteId, true).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosPorUsuarioYCategoria(UUID usuarioId, String categoria) {
        return productoRepository.findByRestauranteUsuarioIdAndCategoria(usuarioId, categoria).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }
    
    // ⭐ NUEVO: Por restauranteId y categoría (para clientes)
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosPorRestauranteYCategoria(UUID restauranteId, String categoria) {
        return productoRepository.findByRestauranteIdAndCategoria(restauranteId, categoria).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscarProductosPorNombre(UUID usuarioId, String nombre) {
        return productoRepository.buscarPorRestauranteUsuarioYNombre(usuarioId, nombre).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }
    
    // ⭐ NUEVO: Buscar por restauranteId y nombre (para clientes)
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscarProductosPorRestauranteYNombre(UUID restauranteId, String nombre) {
        return productoRepository.buscarPorRestauranteIdYNombre(restauranteId, nombre).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<String> obtenerCategoriasPorUsuario(UUID usuarioId) {
        return productoRepository.obtenerCategoriasPorRestauranteUsuario(usuarioId);
    }
    
    // ⭐ NUEVO: Categorías por restauranteId (para clientes)
    @Transactional(readOnly = true)
    public List<String> obtenerCategoriasPorRestaurante(UUID restauranteId) {
        return productoRepository.obtenerCategoriasPorRestauranteId(restauranteId);
    }
    
    @Transactional
    public ProductoResponseDTO actualizarProducto(@NonNull UUID id, ProductoUpdateDTO updateDTO, UUID usuarioId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        if (!producto.getRestaurante().getUsuarioId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para actualizar este producto");
        }
        
        producto.setNombre(updateDTO.getNombre());
        producto.setDescripcion(updateDTO.getDescripcion());
        producto.setPrecio(updateDTO.getPrecio());
        producto.setCategoria(updateDTO.getCategoria());
        producto.setDisponible(updateDTO.getDisponible());
        producto.setImagenUrl(updateDTO.getImagenUrl());
        
        Producto productoActualizado = productoRepository.save(producto);
        return convertirAResponseDTO(productoActualizado);
    }
    
    @Transactional
    public void eliminarProducto(@NonNull UUID id, UUID usuarioId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        if (!producto.getRestaurante().getUsuarioId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar este producto");
        }
        
        productoRepository.deleteById(id);
    }
    
    @Transactional
    public ProductoResponseDTO cambiarDisponibilidad(@NonNull UUID id, Boolean disponible, UUID usuarioId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        if (!producto.getRestaurante().getUsuarioId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para cambiar la disponibilidad de este producto");
        }
        
        producto.setDisponible(disponible);
        Producto productoActualizado = productoRepository.save(producto);
        return convertirAResponseDTO(productoActualizado);
    }
    
    private ProductoResponseDTO convertirAResponseDTO(Producto producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setRestauranteId(producto.getRestaurante().getId());
        dto.setRestauranteNombre("Restaurante");
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setCategoria(producto.getCategoria());
        dto.setDisponible(producto.getDisponible());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setFechaCreacion(producto.getFechaCreacion());
        dto.setFechaActualizacion(producto.getFechaActualizacion());
        return dto;
    }
}
