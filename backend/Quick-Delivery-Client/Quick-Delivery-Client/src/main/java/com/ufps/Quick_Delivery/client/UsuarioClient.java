package com.ufps.Quick_Delivery.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ufps.Quick_Delivery.dto.UsuarioResponse;

@FeignClient(name = "usuario-service", url = "http://localhost:8083")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioResponse obtenerUsuario(@PathVariable("id") UUID usuarioId);

}
