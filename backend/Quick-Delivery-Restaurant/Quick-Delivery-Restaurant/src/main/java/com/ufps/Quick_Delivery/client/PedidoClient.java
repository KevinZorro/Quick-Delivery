package com.ufps.Quick_Delivery.client;

import com.ufps.Quick_Delivery.dto.PedidoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoClient {

    private final RestTemplate restTemplate;

    // ⭐ URL base del microservicio de pedidos
    private final String baseUrl = "http://localhost:8080/api/pedidos";

    public List<PedidoDto> obtenerHistorialPedidos(
            UUID restauranteId,
            String fechaInicio,
            String fechaFin,
            String estado,
            UUID clienteId
    ) {
        String url = baseUrl + "/historial?restauranteId=" + restauranteId;

        if (fechaInicio != null) url += "&fechaInicio=" + fechaInicio;
        if (fechaFin != null) url += "&fechaFin=" + fechaFin;
        if (estado != null) url += "&estado=" + estado;
        if (clienteId != null) url += "&clienteId=" + clienteId;

        ResponseEntity<List<PedidoDto>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<PedidoDto>>() {}
                );

        return response.getBody();
    }

    // ⭐ Cambiar estado del pedido desde restaurante
    public void actualizarEstadoPedido(UUID pedidoId, String nuevoEstado) {
        String url = baseUrl + "/" + pedidoId + "/estado?nuevoEstado=" + nuevoEstado;
        restTemplate.put(url, null);
    }
}
