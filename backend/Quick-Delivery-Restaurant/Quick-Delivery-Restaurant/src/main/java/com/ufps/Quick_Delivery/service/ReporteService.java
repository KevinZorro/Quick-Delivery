package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.config.PedidoFeignClient;
import com.ufps.Quick_Delivery.dto.*;
import com.ufps.Quick_Delivery.model.Producto;
import com.ufps.Quick_Delivery.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final PedidoFeignClient pedidoFeignClient;
    private final ProductoRepository productoRepository;
    private final RestauranteService restauranteService; 


    // Pedidos filtrados por restaurante
    private List<PedidoDto> obtenerPedidosRestaurante(UUID usuaroId) {
        UUID restauranteId = restauranteService.obtenerPorUsuarioId(usuaroId).getId();
        return pedidoFeignClient.obtenerPedidos().stream()
                .filter(p -> p.getRestauranteId().equals(restauranteId))
                .toList();
    }

    // Ingresos por categoría
    public Map<String, Double> obtenerIngresosPorCategoria(UUID restauranteId) {
        Map<String, Double> ingresosPorCategoria = new HashMap<>();

        for (PedidoDto pedido : obtenerPedidosRestaurante(restauranteId)) {
            for (ItemPedidoDto item : pedido.getItems()) {
                Producto producto = productoRepository.findById(item.getProductoId()).orElse(null);
                String categoria = producto != null && producto.getCategoria() != null ? producto.getCategoria() : "Sin categoría";
                ingresosPorCategoria.merge(categoria, item.getSubtotal(), Double::sum);
            }
        }

        return ingresosPorCategoria;
    }

    // Estado de pedidos
    public Map<String, Long> obtenerEstadoPedidos(UUID restauranteId) {
        List<PedidoDto> pedidos = obtenerPedidosRestaurante(restauranteId);

        long completados = pedidos.stream().filter(p -> "COMPLETADO".equalsIgnoreCase(p.getEstado())).count();
        long enCurso = pedidos.stream().filter(p -> !"COMPLETADO".equalsIgnoreCase(p.getEstado())).count();

        return Map.of("completados", completados, "enCurso", enCurso);
    }

    // Platos más vendidos
    public List<Map<String, Object>> obtenerPlatosMasVendidos(UUID restauranteId) {
        Map<UUID, Integer> ventasPorProducto = new HashMap<>();

        for (PedidoDto pedido : obtenerPedidosRestaurante(restauranteId)) {
            for (ItemPedidoDto item : pedido.getItems()) {
                ventasPorProducto.merge(item.getProductoId(), (int) item.getCantidad(), Integer::sum);
            }
        }

        return ventasPorProducto.entrySet().stream()
                .map(entry -> {
                    UUID productoId = entry.getKey();
                    int cantidad = entry.getValue();
                    Producto producto = productoRepository.findById(productoId).orElse(null);

                    Map<String, Object> info = new HashMap<>();
                    info.put("productoId", productoId);
                    info.put("nombre", producto != null ? producto.getNombre() : "Desconocido");
                    info.put("cantidadVendida", cantidad);
                    info.put("categoria", producto != null ? producto.getCategoria() : "Sin categoría");
                    return info;
                })
                .sorted((a, b) -> Integer.compare((int) b.get("cantidadVendida"), (int) a.get("cantidadVendida")))
                .toList();
    }

    // Top 10 productos
    public List<Map<String, Object>> obtenerTop10Productos(UUID restauranteId) {
        return obtenerPlatosMasVendidos(restauranteId).stream().limit(10).toList();
    }

    // Productos con bajas ventas
    public List<Map<String, Object>> obtenerProductosConBajasVentas(UUID restauranteId) {
        return obtenerPlatosMasVendidos(restauranteId).stream()
                .filter(p -> (int) p.get("cantidadVendida") <= 1)
                .toList();
    }

    public long obtenerTotalPedidos(UUID restauranteId) {
        return obtenerPedidosRestaurante(restauranteId).size();
    }

    public double obtenerIngresosTotales(UUID restauranteId) {
        return obtenerPedidosRestaurante(restauranteId).stream()
                .mapToDouble(PedidoDto::getTotal)
                .sum();
    }
}
