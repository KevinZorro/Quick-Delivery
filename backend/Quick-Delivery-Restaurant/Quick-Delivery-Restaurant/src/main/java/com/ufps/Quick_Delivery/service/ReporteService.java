package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.PedidoFeignClient;
import com.ufps.Quick_Delivery.dto.*;
import com.ufps.Quick_Delivery.model.Producto;
import com.ufps.Quick_Delivery.repository.ProductoRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

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
    public Map<String, Double> obtenerIngresosPorCategoria(@NonNull UUID restauranteId) {
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
    public List<Map<String, Object>> obtenerPlatosMasVendidos(@NonNull UUID restauranteId) {
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
    public void exportarExcel(UUID restauranteId, OutputStream outputStream) throws IOException {
    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet("Reporte Ventas");

    Map<String, Double> ingresosPorCategoria = obtenerIngresosPorCategoria(restauranteId);
    Map<String, Long> estadoPedidos = obtenerEstadoPedidos(restauranteId);
    List<Map<String, Object>> platosMasVendidos = obtenerPlatosMasVendidos(restauranteId);

    int rowIdx = 0;

    // Título
    Row titleRow = sheet.createRow(rowIdx++);
    titleRow.createCell(0).setCellValue("REPORTE DE VENTAS");

    rowIdx++;

    // Totales
    Row totalRow = sheet.createRow(rowIdx++);
    totalRow.createCell(0).setCellValue("Total Pedidos");
    totalRow.createCell(1).setCellValue(obtenerTotalPedidos(restauranteId));

    Row ingresoRow = sheet.createRow(rowIdx++);
    ingresoRow.createCell(0).setCellValue("Ingresos Totales");
    ingresoRow.createCell(1).setCellValue(obtenerIngresosTotales(restauranteId));

    rowIdx++;

    // Estado pedidos
    Row estadoHeader = sheet.createRow(rowIdx++);
    estadoHeader.createCell(0).setCellValue("Estado");
    estadoHeader.createCell(1).setCellValue("Cantidad");

    for (var e : estadoPedidos.entrySet()) {
        Row row = sheet.createRow(rowIdx++);
        row.createCell(0).setCellValue(e.getKey());
        row.createCell(1).setCellValue(e.getValue());
    }

    rowIdx += 2;

    // Ingresos por categoría
    Row catHeader = sheet.createRow(rowIdx++);
    catHeader.createCell(0).setCellValue("Categoría");
    catHeader.createCell(1).setCellValue("Ingresos");

    for (var cat : ingresosPorCategoria.entrySet()) {
        Row row = sheet.createRow(rowIdx++);
        row.createCell(0).setCellValue(cat.getKey());
        row.createCell(1).setCellValue(cat.getValue());
    }

    rowIdx += 2;

    // Platos más vendidos
    Row platosHeader = sheet.createRow(rowIdx++);
    platosHeader.createCell(0).setCellValue("Producto");
    platosHeader.createCell(1).setCellValue("Cantidad Vendida");

    for (var plato : platosMasVendidos) {
        Row row = sheet.createRow(rowIdx++);
        row.createCell(0).setCellValue(plato.get("nombre").toString());
        row.createCell(1).setCellValue((int) plato.get("cantidadVendida"));
    }

    workbook.write(outputStream);
    workbook.close();
}
}