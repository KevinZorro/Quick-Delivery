package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Direccion;
import com.ufps.Quick_Delivery.model.TipoReferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, UUID> {
    
    List<Direccion> findByUsuario(UUID usuarioId);
    
    List<Direccion> findByCiudad(String ciudad);
    
    List<Direccion> findByUsuarioAndTipoReferencia(UUID usuarioId, TipoReferencia tipoReferencia);
}
