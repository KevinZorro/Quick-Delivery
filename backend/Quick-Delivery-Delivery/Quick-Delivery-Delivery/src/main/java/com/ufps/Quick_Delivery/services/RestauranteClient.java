package com.ufps.Quick_Delivery.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Service
public class RestauranteClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String BASE_URL = "http://localhost:8081/api/pedidos";

    public void actualizarEstadoPedido(UUID pedidoId, String nuevoEstado) {
        try {
            String url = BASE_URL + "/" + pedidoId + "/estado?nuevoEstado=" + nuevoEstado;
            restTemplate.put(url, null);
            log.info("Estado del pedido {} actualizado a '{}' en Restaurante", pedidoId, nuevoEstado);
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            log.warn("No se encontró el endpoint para actualizar el pedido {} en Restaurante", pedidoId);
        } catch (Exception e) {
            log.error("Error al actualizar el estado del pedido en Restaurante: {}", e.getMessage());
        }
    }
    
}
