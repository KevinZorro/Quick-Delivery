package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.model.Producto;
import com.ufps.Quick_Delivery.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository repository;

    public Producto create(Producto req) {
        Producto p = new Producto();
        // generar uuid como String y setear campos que el repositorio/queries esperan (uuidProducto, uuidRestaurante)
        p.setProductoId(UUID.randomUUID());
        p.setRestauranteId(req.getRestauranteId());
        p.setNombre(req.getNombre());
        p.setDescripcion(req.getDescripcion());
        p.setPrecio(req.getPrecio());
        p.setCategoria(req.getCategoria());
        p.setDisponible(req.getDisponible() != null ? req.getDisponible() : Boolean.TRUE);
        p.setImagenUrl(req.getImagenUrl());
        return repository.save(p);
    }

    public Producto update(UUID uuidProducto, Producto req) {
        Producto p = findByUuidProducto(uuidProducto);
        p.setNombre(req.getNombre());
        p.setDescripcion(req.getDescripcion());
        p.setPrecio(req.getPrecio());
        p.setCategoria(req.getCategoria());
        if (req.getDisponible() != null) p.setDisponible(req.getDisponible());
        p.setImagenUrl(req.getImagenUrl());
        return repository.save(p);
    }

    public void delete(UUID uuidProducto) {
        Producto p = findByUuidProducto(uuidProducto);
        repository.delete(p);
    }

    @Transactional(readOnly = true)
    public List<Producto> findByRestaurante(UUID uuidRestaurante) {
        return repository.findByUuidRestaurante(uuidRestaurante);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(UUID uuidRestaurante, String nombre) {
        return repository.findByUuidRestauranteAndNombreContainingIgnoreCase(uuidRestaurante, nombre);
    }

    @Transactional(readOnly = true)
    public List<Producto> filtrarPorPrecio(UUID uuidRestaurante, BigDecimal min, BigDecimal max) {
        return repository.findByUuidRestauranteAndPrecioBetween(uuidRestaurante, min, max);
    }

    @Transactional(readOnly = true)
    public List<Producto> porCategoria(UUID uuidRestaurante, String categoria) {
        return repository.findByUuidRestauranteAndCategoria(uuidRestaurante, categoria);
    }

    @Transactional(readOnly = true)
    public List<Producto> disponibles(UUID uuidRestaurante) {
        return repository.findByUuidRestauranteAndDisponible(uuidRestaurante, true);
    }

    @Transactional(readOnly = true)
    public Producto findByUuidProducto(UUID uuidProducto) {
        return repository.findByUuidProducto(uuidProducto)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }
}