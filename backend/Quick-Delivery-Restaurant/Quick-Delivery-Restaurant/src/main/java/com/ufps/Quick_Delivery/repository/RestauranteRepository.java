package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Categoria;
import com.ufps.Quick_Delivery.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, UUID> {
    
    // Buscar restaurantes por usuario
    List<Restaurante> findByUsuarioId(UUID usuarioId);
    
    // Validar duplicados por usuario y descripción
    Optional<Restaurante> findByUsuarioIdAndDescripcion(UUID usuarioId, String descripcion);
    
    // Buscar por categoría
    List<Restaurante> findByCategoria(Categoria categoria);
    
    // Buscar restaurantes con calificación mayor o igual a un valor
    List<Restaurante> findByCalificacionPromedioGreaterThanEqual(Double calificacion);
    
    // Buscar por usuario y categoría
    List<Restaurante> findByUsuarioIdAndCategoria(UUID usuarioId, Categoria categoria);
    
    // Verificar si existe por usuario y descripción
    boolean existsByUsuarioIdAndDescripcion(UUID usuarioId, String descripcion);
}
