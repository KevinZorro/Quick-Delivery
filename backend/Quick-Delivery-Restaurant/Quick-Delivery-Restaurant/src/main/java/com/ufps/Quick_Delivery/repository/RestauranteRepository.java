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
    
    // Buscar restaurante por usuarioId (relación 1:1)
    Optional<Restaurante> findByUsuarioId(UUID usuarioId);
    
    // Buscar por categoría
    List<Restaurante> findByCategoria(Categoria categoria);
    
    // Buscar por calificación mayor o igual
    List<Restaurante> findByCalificacionPromedioGreaterThanEqual(Double calificacion);
    
    // Verificar si existe por usuarioId
    boolean existsByUsuarioId(UUID usuarioId);


}

//hola