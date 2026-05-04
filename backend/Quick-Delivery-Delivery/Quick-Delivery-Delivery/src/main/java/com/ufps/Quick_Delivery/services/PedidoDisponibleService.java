package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.client.*;
import com.ufps.Quick_Delivery.dto.*;
import com.ufps.Quick_Delivery.models.DeliveryUser;
import com.ufps.Quick_Delivery.repository.DeliveryUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoDisponibleService {

        private final RestauranteClient restauranteClient;
        private final EdgeClient edgeClient;
        private final DeliveryUserRepository deliveryUserRepository;
        private final ClienteClient clienteclient;

        /**
         * Obtener pedidos asignados a un delivery específico por usuarioId del delivery
         */
        public List<PedidoDisponibleDto> obtenerPedidosEnCursoPorUsuarioId(UUID usuarioId) {
                log.info("Obteniendo pedidos en curso para usuario delivery: {}", usuarioId);

                // 1. Obtener el DeliveryUser por usuarioId
                DeliveryUser deliveryUser = deliveryUserRepository.findByUsuarioId(usuarioId)
                                .orElseThrow(() -> new RuntimeException("DeliveryUser no encontrado"));

                UUID deliveryId = deliveryUser.getId(); // ⭐ ID del delivery_user (no usuarioId)
                log.info("Delivery ID encontrado: {}", deliveryId);

                // 2. Obtener todos los pedidos
                ResponseEntity<List<PedidoResponse>> pedidosResp = clienteclient.listarPedidos();

                if (pedidosResp.getBody() == null) {
                        return Collections.emptyList();
                }

                // 3. Filtrar: estado CON_EL_REPARTIDOR Y repartidorId = deliveryId (NO
                // usuarioId)
                List<PedidoResponse> pedidosEnCurso = pedidosResp.getBody().stream()
                                .filter(p -> "CON_EL_REPARTIDOR".equals(p.getEstado())
                                                && deliveryId.equals(p.getRepartidorId())) // ⭐ deliveryId del
                                                                                           // DeliveryUser
                                .collect(Collectors.toList());

                log.info("Encontrados {} pedidos en curso para delivery ID: {}",
                                pedidosEnCurso.size(), deliveryId);

                // 4. Mapear a DTO simple
                return pedidosEnCurso.stream()
                                .map(this::mapToPedidoDisponibleDto)
                                .collect(Collectors.toList());
        }

        /**
         * Obtener pedidos disponibles para un repartidor según su ubicación
         */
        public List<PedidoDisponibleDto> obtenerPedidosDisponibles(UUID usuarioId) {

                log.info("Buscando pedidos disponibles para repartidor {}", usuarioId);

                // 1️⃣ Buscar repartidor
                DeliveryUser deliveryUser = deliveryUserRepository.findByUsuarioId(usuarioId)
                                .orElse(null);

                if (deliveryUser == null) {
                        log.warn("Repartidor no encontrado, creando registro...");
                        deliveryUser = DeliveryUser.builder()
                                        .usuarioId(usuarioId)
                                        .rangoKm(20.0)
                                        .calificacionPromedio(0.0)
                                        .build();
                        deliveryUserRepository.save(deliveryUser);
                        return Collections.emptyList();
                }

                // 2️⃣ Obtener ubicación actual desde EDGE
                DireccionResponse direccionActual = edgeClient.obtenerDireccionActualUsuario(usuarioId);

                if (direccionActual == null || direccionActual.getCoordenadas() == null ||
                                direccionActual.getCoordenadas().trim().isEmpty()) {
                        log.warn("El repartidor {} no tiene coordenadas configuradas", usuarioId);
                        return Collections.emptyList();
                }

                String[] coords = direccionActual.getCoordenadas().split(",");
                double latRepartidor = Double.parseDouble(coords[0].trim());
                double lngRepartidor = Double.parseDouble(coords[1].trim());

                double rangoKm = deliveryUser.getRangoKm() != null ? deliveryUser.getRangoKm() : 20.0;

                // 3️⃣ Obtener pedidos desde restaurante-service
                ResponseEntity<List<PedidoResponse>> pedidosResp = clienteclient.listarPedidos();

                if (pedidosResp.getBody() == null)
                        return Collections.emptyList();

                List<PedidoResponse> disponibles = pedidosResp.getBody().stream()
                                .filter(p -> "CON_EL_REPARTIDOR".equals(p.getEstado()) && p.getRepartidorId() == null)
                                .collect(Collectors.toList());

                List<PedidoDisponibleDto> resultado = new ArrayList<>();

                // 4️⃣ Recorrer pedidos
                for (PedidoResponse pedido : disponibles) {

                        if (pedido.getDireccionEntregaId() == null)
                                continue;

                        // Dirección de entrega desde edge
                        ResponseEntity<DireccionResponse> dirResp = edgeClient
                                        .obtenerDireccion(pedido.getDireccionEntregaId());

                        DireccionResponse direccion = dirResp.getBody();

                        if (direccion == null || direccion.getCoordenadas() == null)
                                continue;

                        String[] destCoords = direccion.getCoordenadas().split(",");
                        String destLat = destCoords[0].trim();
                        String destLng = destCoords[1].trim();

                        // 5️⃣ Calcular distancia desde EDGE via Maps
                        DistanceMatrixRequest req = new DistanceMatrixRequest();
                        req.setOriginLat(String.valueOf(latRepartidor));
                        req.setOriginLng(String.valueOf(lngRepartidor));
                        req.setDestinationLat(destLat);
                        req.setDestinationLng(destLng);

                        DistanceMatrixResponse dist = edgeClient.calcularDistancia(req).getBody();

                        if (dist == null || dist.getDistanceValue() == null)
                                continue;

                        double distanciaKm = dist.getDistanceValue() / 1000.0;

                        // Validar rango
                        if (distanciaKm > rangoKm)
                                continue;

                        // 6️⃣ Construir DTO final
                        PedidoDisponibleDto dto = PedidoDisponibleDto.builder()
                                        .id(pedido.getId())
                                        .restauranteId(pedido.getRestauranteId())
                                        .direccionEntregaId(pedido.getDireccionEntregaId())
                                        .direccionEntrega(direccion.getCalle() + ", " +
                                                        direccion.getBarrio() + ", " + direccion.getCiudad())
                                        .coordenadasEntrega(direccion.getCoordenadas())
                                        .distanciaKm(distanciaKm)
                                        .distanciaTexto(dist.getDistance())
                                        .tiempoEstimado(dist.getDuration())
                                        .estado(pedido.getEstado())
                                        .total(pedido.getTotal())
                                        .fechaCreacion(pedido.getFechaCreacion())
                                        .items(mapearItems(pedido))
                                        .build();

                        resultado.add(dto);
                }

                // Ordenar por distancia
                resultado.sort(Comparator.comparing(PedidoDisponibleDto::getDistanciaKm));

                return resultado;
        }

        private List<ItemPedidoDto> mapearItems(PedidoResponse pedido) {
                if (pedido.getItems() == null)
                        return Collections.emptyList();

                return pedido.getItems().stream()
                                .map(i -> ItemPedidoDto.builder()
                                                .id(i.getId())
                                                .productoId(i.getProductoId())
                                                .cantidad(i.getCantidad())
                                                .precioUnidad(i.getPrecioUnidad())
                                                .subtotal(i.getSubtotal())
                                                .build())
                                .collect(Collectors.toList());
        }

        /**
         * Aceptar un pedido
         */
        public void aceptarPedido(UUID usuarioId, UUID pedidoId) {

                DeliveryUser deliveryUser = deliveryUserRepository.findByUsuarioId(usuarioId)
                                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

                // Obtener pedido en restaurante-service
                PedidoResponse pedido = clienteclient.obtenerPedidoPorId(pedidoId);

                if (pedido == null)
                        throw new RuntimeException("Pedido no encontrado");
                log.info("Pedido {} encontrado: estado={}", pedidoId, pedido.getEstado());
                clienteclient.asignarRepartidor(pedidoId, deliveryUser.getId());

                log.info("Pedido {} aceptado por {}", pedidoId, usuarioId);
        }

private PedidoDisponibleDto mapToPedidoDisponibleDto(PedidoResponse p) {
    return PedidoDisponibleDto.builder()
            .id(p.getId())
            .restauranteId(p.getRestauranteId())
            .direccionEntregaId(p.getDireccionEntregaId())
            .total(p.getTotal())
            .estado(p.getEstado())
            .fechaCreacion(p.getFechaCreacion())
            .items(p.getItems() != null 
                    ? p.getItems().stream()
                        .map(i -> ItemPedidoDto.builder()
                                .id(i.getId())
                                .productoId(i.getProductoId())
                                .cantidad(i.getCantidad())
                                .precioUnidad(i.getPrecioUnidad())
                                .subtotal(i.getSubtotal())
                                .build())
                        .collect(Collectors.toList())
                    : Collections.emptyList())
            .build();
}



}
