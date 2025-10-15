package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {


    
    Optional<Producto> findByUuidProducto(String uuidProducto);
    
    List<Producto> findByUuidRestaurante(String uuidRestaurante);
    
    List<Producto> findByUuidRestauranteAndDisponible(String uuidRestaurante, Boolean disponible);
    
    List<Producto> findByCategoria(String categoria);
    
    List<Producto> findByUuidRestauranteAndCategoria(String uuidRestaurante, String categoria);
    
    @Query("SELECT p FROM Producto p WHERE p.uuidRestaurante = :uuidRestaurante AND p.precio BETWEEN :precioMin AND :precioMax")
    List<Producto> findByUuidRestauranteAndPrecioBetween(
            @Param("uuidRestaurante") String uuidRestaurante,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax
    );
    
    @Query("SELECT p FROM Producto p WHERE p.uuidRestaurante = :uuidRestaurante AND p.nombre ILIKE %:nombre%")
    List<Producto> findByUuidRestauranteAndNombreContainingIgnoreCase(
            @Param("uuidRestaurante") String uuidRestaurante,
            @Param("nombre") String nombre
    );
    
    boolean existsByUuidProducto(String uuidProducto);
    
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.uuidRestaurante = :uuidRestaurante AND p.disponible = true")
    Long countProductosDisponiblesByRestaurante(@Param("uuidRestaurante") String uuidRestaurante);
}
