package com.ufps.Quick_Delivery.repartidor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufps.Quick_Delivery.repartidor.model.Repartidor;

/**
 * Repositorio para la entidad Repartidor.
 * Proporciona métodos CRUD y métodos de consulta personalizados a la base de datos.
 */

public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {
    
    /**
     * Busca un repartidor por su dirección de email.
     * @param email La dirección de email a buscar.
     * @return Un Optional que contiene el repartidor si es encontrado.
     */
    
    Optional<Repartidor> findByEmail(String email);
}