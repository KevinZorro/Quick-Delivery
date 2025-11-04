package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ventas/{restauranteId}")
    public Map<String, Object> obtenerReporteVentas(@PathVariable("restauranteId") UUID restauranteId) {
        Map<String, Object> reporte = new HashMap<>();

        reporte.put("platosMasVendidos", reporteService.obtenerPlatosMasVendidos(restauranteId));
        reporte.put("top10Productos", reporteService.obtenerTop10Productos(restauranteId));
        reporte.put("productosBajasVentas", reporteService.obtenerProductosConBajasVentas(restauranteId));
        reporte.put("ingresosPorCategoria", reporteService.obtenerIngresosPorCategoria(restauranteId));
        reporte.put("estadoPedidos", reporteService.obtenerEstadoPedidos(restauranteId));
        reporte.put("totalPedidos", reporteService.obtenerTotalPedidos(restauranteId));
        reporte.put("ingresosTotales", reporteService.obtenerIngresosTotales(restauranteId));

        return reporte;
    }
}
