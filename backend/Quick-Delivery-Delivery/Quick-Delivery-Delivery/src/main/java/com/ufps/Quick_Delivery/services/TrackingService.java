package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.client.*;
import com.ufps.Quick_Delivery.dto.TrackingDataResponse;
import com.ufps.Quick_Delivery.models.DeliveryUser;
import com.ufps.Quick_Delivery.repository.DeliveryUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    private final RestauranteClient restauranteClient;
    private final EdgeClient edgeClient;
    private final DeliveryUserRepository deliveryUserRepository;
    private final ClienteClient clienteclient;

    public TrackingDataResponse obtenerTrackingData(UUID pedidoId) {

        log.info("🔍 Obteniendo tracking data para pedido {}", pedidoId);

        // 1️⃣ Obtener pedido desde restaurante-service
        var pedido = clienteclient.obtenerPedidoPorId(pedidoId);
        if (pedido == null) {
            throw new RuntimeException("Pedido no encontrado");
        }

        if (!"CON_EL_REPARTIDOR".equals(pedido.getEstado())) {
            throw new RuntimeException("Tracking solo disponible para pedidos CON_EL_REPARTIDOR");
        }

        // 2️⃣ Obtener repartidor → deliveryUser → usuarioId
        UUID deliveryId = pedido.getRepartidorId();

        DeliveryUser deliveryUser = deliveryUserRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("DeliveryUser no encontrado: " + deliveryId));

        UUID usuarioId = deliveryUser.getUsuarioId();

        // 3️⃣ Obtener ubicación actual del repartidor desde EDGE
        var ubicacionRepartidor = edgeClient.obtenerDireccionActualUsuario(usuarioId);

        if (ubicacionRepartidor == null || ubicacionRepartidor.getCoordenadas() == null) {
            throw new RuntimeException("Ubicación del repartidor no disponible");
        }

        String[] repCoords = ubicacionRepartidor.getCoordenadas().split(",");
        double repartidorLat = Double.parseDouble(repCoords[0].trim());
        double repartidorLng = Double.parseDouble(repCoords[1].trim());

        // 4️⃣ Obtener dirección de entrega desde EDGE
        var direccionEntrega = edgeClient.obtenerDireccionPorId(pedido.getDireccionEntregaId());

        if (direccionEntrega == null || direccionEntrega.getCoordenadas() == null) {
            throw new RuntimeException("Dirección de entrega no disponible");
        }

        String[] cliCoords = direccionEntrega.getCoordenadas().split(",");
        double clienteLat = Double.parseDouble(cliCoords[0].trim());
        double clienteLng = Double.parseDouble(cliCoords[1].trim());

        // 5️⃣ Construir respuesta
        return TrackingDataResponse.builder()
                .repartidorLat(repartidorLat)
                .repartidorLng(repartidorLng)
                .clienteLat(clienteLat)
                .clienteLng(clienteLng)
                .build();
    }
}
