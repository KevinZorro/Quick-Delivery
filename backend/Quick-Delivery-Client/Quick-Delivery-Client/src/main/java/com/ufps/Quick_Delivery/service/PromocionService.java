package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.RestauranteDto;
import com.ufps.Quick_Delivery.model.EstadoPromocion;
import com.ufps.Quick_Delivery.model.Promocion;
import com.ufps.Quick_Delivery.model.UsoPromocion;
import com.ufps.Quick_Delivery.repository.PromocionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.ufps.Quick_Delivery.repository.UsoPromocionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import com.ufps.Quick_Delivery.client.RestauranteClient;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private UsoPromocionRepository usoPromocionRepository;

    @Autowired
    private RestauranteClient restauranteClient;

    public Optional<Promocion> obtenerPorCodigo(String codigo) {
    return promocionRepository.findByCodigo(codigo);
}

    public boolean yaUsoPromocion(UUID promocionId, UUID clienteId) {
        return usoPromocionRepository.existsByPromocionIdAndClienteId(promocionId, clienteId);
    }

    public void registrarUsoParaCliente(Promocion promo, UUID clienteId, UUID restauranteId) {
        UsoPromocion uso = new UsoPromocion();
        uso.setPromocionId(promo.getId());
        uso.setClienteId(clienteId);
        uso.setFechaUso(LocalDateTime.now());
        usoPromocionRepository.save(uso);

        registrarUso(promo, clienteId, restauranteId); // tu lógica actual de restar cantidadUsos, etc.
    }


public Promocion actualizarDatosPromocion(UUID id, EstadoPromocion estado,
                                          LocalDate fechaExpiracion, Integer cantidadUsos) {
    Promocion promo = promocionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));

    if (fechaExpiracion != null) {
        if (fechaExpiracion.isBefore(LocalDate.now())) {
            promo.setEstado(EstadoPromocion.EXPIRADA);
        } else {
            promo.setFechaExpiracion(fechaExpiracion);
            if (estado != null) {
                promo.setEstado(estado);
            }
        }
    } else if (estado != null) {
        promo.setEstado(estado);
    }

    if (cantidadUsos != null) {
        promo.setCantidadUsos(cantidadUsos);
    }

    return promocionRepository.save(promo);
}


    public List<Promocion> getAllPromociones() {
        return promocionRepository.findAll();
    }
    public UUID obtenerRestauranteIdPorUsuario(UUID usuarioId) {
        RestauranteDto dto = restauranteClient.obtenerRestaurantePorUsuarioId(usuarioId);
        return dto.getId(); // asegúrate que RestauranteDto tenga getId()
    }
    

    public List<Promocion> obtenerPromocionesRestaurante(UUID restauranteId) {
    return promocionRepository.findByRestauranteId(restauranteId);
}

    public Optional<Promocion> getPromocionById(UUID id) {
        return promocionRepository.findById(id);
    }

    public Promocion savePromocion(Promocion promocion) {
        return promocionRepository.save(promocion);
    }

public Promocion crearPromocion(Promocion promocion, UUID restauranteId) {
    // Validar fecha Expiración
    if (promocion.getFechaExpiracion() != null 
         && promocion.getFechaExpiracion().isBefore(LocalDate.now())) {
        throw new IllegalArgumentException("La fecha de expiración no puede ser menor a la fecha actual");
    }
    promocion.setRestauranteId(restauranteId);
    promocion.setEstado(EstadoPromocion.ACTIVA);
    promocion.setCantidadUsos(promocion.getCantidadUsos()); // 0 si no limitas por usos
    return promocionRepository.save(promocion);
}


@Transactional(readOnly = true)
public boolean puedeUsarseCompleto(Promocion p, UUID clienteId, UUID restauranteId) {
    // Verificar estado
    if (p.getEstado() != EstadoPromocion.ACTIVA) {
        return false;
    }
    
    // ⭐ NUEVA VALIDACIÓN: Restaurante correcto
    if (!p.getRestauranteId().equals(restauranteId)) {
        return false;
    }
    
    // Verificar fecha de expiración
    if (p.getFechaExpiracion() != null && 
        p.getFechaExpiracion().isBefore(LocalDate.now())) {
        p.setEstado(EstadoPromocion.EXPIRADA);
        promocionRepository.save(p);
        return false;
    }
    
    // Verificar si cliente ya la usó
    if (yaUsoPromocion(p.getId(), clienteId)) {
        return false;
    }
    
    // Verificar límite de usos totales
    if (p.getCantidadUsos() > 0 && p.getCantidadUsos() <= 0) {
        p.setEstado(EstadoPromocion.AGOTADA);
        promocionRepository.save(p);
        return false;
    }
    
    return true;
}


    public void registrarUso(Promocion p, UUID clienteId, UUID restauranteId) {
        if (!puedeUsarseCompleto(p, clienteId, restauranteId)) return;
        p.setCantidadUsos(p.getCantidadUsos() - 1);
        if (p.getCantidadUsos() == 0) {
            p.setEstado(EstadoPromocion.AGOTADA);
        }
        promocionRepository.save(p);
    }

    public void deletePromocion(UUID id) {
        promocionRepository.deleteById(id);
    }
}
