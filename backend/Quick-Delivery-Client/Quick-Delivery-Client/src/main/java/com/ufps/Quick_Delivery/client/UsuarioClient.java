package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

import com.ufps.Quick_Delivery.dto.UsuarioDto;

@FeignClient(name = "usuario-service", url = "http://localhost:8083")
public interface UsuarioClient {
    @GetMapping("/usuarios/{id}")
    UsuarioDto obtenerUsuarioPorId(@PathVariable("id") UUID id);
}
