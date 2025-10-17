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

    public List<Producto> getAllPedidos() {
        return repository.findAll();
    }

    public Producto create(Producto req) {
        Producto p = new Producto();
        p.setRestaurante(req.getRestaurante());
        p.setNombre(req.getNombre());
        p.setDescripcion(req.getDescripcion());
        p.setPrecio(req.getPrecio());
        p.setCategoria(req.getCategoria());
        p.setDisponible(req.getDisponible() != null ? req.getDisponible() : Boolean.TRUE);
        p.setImagenUrl(req.getImagenUrl());
        System.err.println("perrita?");
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
        return repository.findByRestauranteId(uuidRestaurante);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(UUID uuidRestaurante, String nombre) {
        return repository.findByRestauranteIdAndNombreContainingIgnoreCase(uuidRestaurante, nombre);
    }

    @Transactional(readOnly = true)
    public List<Producto> filtrarPorPrecio(UUID uuidRestaurante, BigDecimal min, BigDecimal max) {
        return repository.findByRestauranteIdAndPrecioBetween(uuidRestaurante, min, max);
    }

    @Transactional(readOnly = true)
    public List<Producto> porCategoria(UUID uuidRestaurante, String categoria) {
        return repository.findByRestauranteIdAndCategoria(uuidRestaurante, categoria);
    }

    @Transactional(readOnly = true)
    public Producto findByUuidProducto(UUID uuidProducto) {
        return repository.findById(uuidProducto)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }
}