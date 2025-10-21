package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {

    List<Producto> findByRestauranteId(UUID restauranteId);

    List<Producto> findByCategoria(String categoria);

    List<Producto> findByRestauranteIdAndCategoria(UUID restauranteId, String categoria);

    List<Producto> findByRestauranteIdAndPrecioBetween(UUID restauranteId, BigDecimal precioMin, BigDecimal precioMax);

    List<Producto> findByRestauranteIdAndNombreContainingIgnoreCase(UUID restauranteId, String nombre);

    Long countByRestauranteIdAndDisponibleTrue(UUID restauranteId);
}