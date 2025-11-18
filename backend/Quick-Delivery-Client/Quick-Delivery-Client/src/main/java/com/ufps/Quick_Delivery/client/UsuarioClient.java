package com.ufps.Quick_Delivery.client;

import com.ufps.Quick_Delivery.dto.UsuarioResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "usuario-service", url = "http://localhost:8083")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioResponse obtenerUsuario(@PathVariable("id") UUID usuarioId);

}
