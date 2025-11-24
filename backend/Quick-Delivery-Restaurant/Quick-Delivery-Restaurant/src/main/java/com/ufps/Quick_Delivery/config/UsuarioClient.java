package com.ufps.Quick_Delivery.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "edge-service", url = "${edge-service.url}")
public interface UsuarioClient {
    
    @GetMapping("/api/usuarios/activos/rol/{rol}")
    List<UUID> obtenerUsuariosActivosPorRol(@PathVariable("rol") String rol);
}

