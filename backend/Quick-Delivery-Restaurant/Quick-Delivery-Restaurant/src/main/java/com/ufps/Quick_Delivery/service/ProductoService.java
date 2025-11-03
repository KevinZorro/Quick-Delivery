package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.model.Producto;
import com.ufps.Quick_Delivery.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo) {
        this.repo = repo;
    }

    public List<Producto> getAllPedidos() {
        return repo.findAll();
    }

    public List<Producto> findByRestaurante(UUID restauranteId) {
        return repo.findByRestauranteId(restauranteId);
    }

    public Producto findByUuidProducto(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public Producto create(Producto producto) {
        return repo.save(producto);
    }

    public Producto update(UUID id, Producto producto) {
        producto.setId(id); // Asegurar que usa el ID correcto
        return repo.save(producto);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }

    public List<Producto> buscarPorNombre(UUID restauranteId, String nombre) {
        return repo.findByRestauranteIdAndNombreContainingIgnoreCase(restauranteId, nombre);
    }

    public List<Producto> filtrarPorPrecio(UUID restauranteId, BigDecimal min, BigDecimal max) {
        return repo.findByRestauranteIdAndPrecioBetween(restauranteId, min, max);
    }

    public List<Producto> porCategoria(UUID restauranteId, String categoria) {
        return repo.findByRestauranteIdAndCategoria(restauranteId, categoria);
    }
}
