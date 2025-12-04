package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.Direccion;
import com.ufps.Quick_Delivery.model.TipoReferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, UUID> {

    @Query("SELECT d FROM Direccion d WHERE d.usuario.id = :usuarioId " +
       "AND d.coordenadas IS NOT NULL ") 
Optional<Direccion> findDireccionRecienteConCoordenadasByUsuarioId(@Param("usuarioId") UUID usuarioId);


    
    // ⭐ CORRECTO: Usar Usuario_Id cuando comparas con UUID
    List<Direccion> findByUsuarioId(UUID usuarioId);

    Optional<Direccion> findFirstByUsuarioId(UUID usuarioId);

    
    // ⭐ CORRECTO: Usar Usuario_Id
    long countByUsuarioId(UUID usuarioId);
    
    // ⭐ CORRECTO: Usar Usuario_Id si existe este método
    List<Direccion> findByUsuarioIdAndTipoReferencia(UUID usuarioId, TipoReferencia tipoReferencia);
}
