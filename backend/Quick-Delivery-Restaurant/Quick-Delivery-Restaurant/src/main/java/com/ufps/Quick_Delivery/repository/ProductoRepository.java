package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {
    
    // ⭐ Buscar por usuarioId (para el restaurante autenticado)
    List<Producto> findByRestauranteUsuarioId(UUID usuarioId);
    
    List<Producto> findByRestauranteUsuarioIdAndDisponible(UUID usuarioId, Boolean disponible);
    
    // ⭐ NUEVO: Buscar por restauranteId (para clientes que quieren ver menú)
    List<Producto> findByRestauranteId(UUID restauranteId);
    
    // ⭐ NUEVO: Buscar por restauranteId solo disponibles
    List<Producto> findByRestauranteIdAndDisponible(UUID restauranteId, Boolean disponible);
    
    List<Producto> findByCategoria(String categoria);
    
    List<Producto> findByRestauranteUsuarioIdAndCategoria(UUID usuarioId, String categoria);
    
    // ⭐ NUEVO: Por restauranteId y categoría
    List<Producto> findByRestauranteIdAndCategoria(UUID restauranteId, String categoria);
    
    @Query("SELECT p FROM Producto p WHERE p.restaurante.usuarioId = :usuarioId AND p.nombre LIKE %:nombre%")
    List<Producto> buscarPorRestauranteUsuarioYNombre(
            @Param("usuarioId") UUID usuarioId, 
            @Param("nombre") String nombre
    );
    
    // ⭐ NUEVO: Por restauranteId y nombre
    @Query("SELECT p FROM Producto p WHERE p.restaurante.id = :restauranteId AND p.nombre LIKE %:nombre%")
    List<Producto> buscarPorRestauranteIdYNombre(
            @Param("restauranteId") UUID restauranteId, 
            @Param("nombre") String nombre
    );
    
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.restaurante.usuarioId = :usuarioId")
    List<String> obtenerCategoriasPorRestauranteUsuario(@Param("usuarioId") UUID usuarioId);
    
    // ⭐ NUEVO: Categorías por restauranteId
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.restaurante.id = :restauranteId")
    List<String> obtenerCategoriasPorRestauranteId(@Param("restauranteId") UUID restauranteId);
    
    boolean existsByIdAndRestauranteUsuarioId(UUID id, UUID usuarioId);
}
