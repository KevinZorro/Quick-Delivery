package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.client.*;
import com.ufps.Quick_Delivery.dto.ItemPedidoDto;
import com.ufps.Quick_Delivery.dto.PedidoDisponibleDto;
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

    private final PedidoClient pedidoClient;
    private final DireccionClient direccionClient;
    private final GoogleMapsClient googleMapsClient;
    private final DeliveryUserRepository deliveryUserRepository;

    /**
     * Obtener pedidos disponibles para un repartidor según su ubicación
     */
    public List<PedidoDisponibleDto> obtenerPedidosDisponibles(UUID usuarioId) {
        log.info("Buscando pedidos disponibles para repartidor: {}", usuarioId);

        try {
            // 1. Obtener el delivery user y su ubicación
            DeliveryUser deliveryUser = deliveryUserRepository.findByUsuarioId(usuarioId)
                    .orElse(null);
            
            if (deliveryUser == null) {
                log.warn("Repartidor no encontrado con usuarioId: {}. Creando registro...", usuarioId);
                // Crear un delivery user básico si no existe
                deliveryUser = DeliveryUser.builder()
                        .usuarioId(usuarioId)
                        .rangoKm(10.0)
                        .calificacionPromedio(0.0)
                        .build();
                deliveryUser = deliveryUserRepository.save(deliveryUser);
                log.info("Repartidor creado con ID: {}", deliveryUser.getId());
                // Retornar lista vacía ya que no tiene ubicación
                return Collections.emptyList();
            }

            if (deliveryUser.getLatitud() == null || deliveryUser.getLongitud() == null) {
                log.warn("El repartidor {} no tiene ubicación GPS configurada", usuarioId);
                return Collections.emptyList();
            }

            Double rangoKm = deliveryUser.getRangoKm() != null ? deliveryUser.getRangoKm() : 10.0;
            log.info("Rango de búsqueda: {} km", rangoKm);

            // 2. Obtener todos los pedidos
            log.info("Llamando a pedidoClient.listarPedidos()...");
            List<PedidoResponse> todosLosPedidos;
            
            try {
                ResponseEntity<List<PedidoResponse>> pedidosResponse = pedidoClient.listarPedidos();
                
                if (pedidosResponse == null) {
                    log.error("La respuesta de pedidoClient.listarPedidos() es null");
                    return Collections.emptyList();
                }
                
                if (pedidosResponse.getBody() == null || pedidosResponse.getBody().isEmpty()) {
                    log.info("No hay pedidos disponibles");
                    return Collections.emptyList();
                }

                todosLosPedidos = pedidosResponse.getBody();
                log.info("Se obtuvieron {} pedidos del servicio Client", todosLosPedidos.size());
            } catch (Exception e) {
                log.error("Error al llamar a pedidoClient.listarPedidos(): {}", e.getMessage(), e);
                throw new RuntimeException("Error al conectar con el servicio de pedidos: " + e.getMessage(), e);
            }

            // 3. Filtrar pedidos disponibles (estado EN_COCINA y sin repartidor asignado)
            List<PedidoResponse> pedidosDisponibles = todosLosPedidos.stream()
                    .filter(p -> "EN_COCINA".equals(p.getEstado()) && p.getRepartidorId() == null)
                    .collect(Collectors.toList());

            log.info("Encontrados {} pedidos en estado EN_COCINA sin repartidor", pedidosDisponibles.size());

            // 4. Para cada pedido, obtener la dirección de entrega y calcular distancia
            List<PedidoDisponibleDto> pedidosConDistancia = new ArrayList<>();

            for (PedidoResponse pedido : pedidosDisponibles) {
                try {
                    if (pedido.getDireccionEntregaId() == null) {
                        log.warn("Pedido {} no tiene dirección de entrega", pedido.getId());
                        continue;
                    }

                    // Obtener dirección de entrega
                    DireccionResponse direccion;
                    try {
                        ResponseEntity<DireccionResponse> direccionResponse = direccionClient.obtenerDireccion(
                                pedido.getDireccionEntregaId()
                        );
                        direccion = direccionResponse.getBody();
                    } catch (Exception e) {
                        log.warn("Error al obtener dirección para pedido {}: {}", pedido.getId(), e.getMessage());
                        continue;
                    }
                    
                    if (direccion == null || 
                        direccion.getCoordenadas() == null ||
                        direccion.getCoordenadas().trim().isEmpty()) {
                        log.warn("Pedido {} no tiene coordenadas de entrega", pedido.getId());
                        continue;
                    }
                    String[] coordenadas = direccion.getCoordenadas().split(",");
                    if (coordenadas.length != 2) {
                        log.warn("Coordenadas inválidas para pedido {}: {}", pedido.getId(), direccion.getCoordenadas());
                        continue;
                    }

                    String destLat = coordenadas[0].trim();
                    String destLng = coordenadas[1].trim();

                    // Calcular distancia
                    DistanceMatrixResponse distancia;
                    try {
                        DistanceMatrixRequest distanceRequest = new DistanceMatrixRequest();
                        distanceRequest.setOriginLat(deliveryUser.getLatitud().toString());
                        distanceRequest.setOriginLng(deliveryUser.getLongitud().toString());
                        distanceRequest.setDestinationLat(destLat);
                        distanceRequest.setDestinationLng(destLng);

                        ResponseEntity<DistanceMatrixResponse> distanceResponse = googleMapsClient.calcularDistancia(distanceRequest);
                        distancia = distanceResponse.getBody();
                    } catch (Exception e) {
                        log.warn("Error al calcular distancia para pedido {}: {}", pedido.getId(), e.getMessage());
                        continue;
                    }
                    
                    if (distancia == null || distancia.getDistanceValue() == null) {
                        log.warn("No se pudo calcular distancia para pedido {}", pedido.getId());
                        continue;
                    }

                    double distanciaKm = distancia.getDistanceValue() / 1000.0; // convertir metros a km

                    // Filtrar por rango
                    if (distanciaKm > rangoKm) {
                        log.debug("Pedido {} está fuera del rango ({} km > {} km)", pedido.getId(), distanciaKm, rangoKm);
                        continue;
                    }

                    // Construir dirección completa
                    String direccionCompleta = String.format("%s, %s, %s",
                            direccion.getCalle(),
                            direccion.getBarrio(),
                            direccion.getCiudad());

                    // Convertir items
                    List<ItemPedidoDto> items = pedido.getItems() != null ?
                            pedido.getItems().stream()
                                    .map(item -> ItemPedidoDto.builder()
                                            .id(item.getId())
                                            .productoId(item.getProductoId())
                                            .cantidad(item.getCantidad())
                                            .precioUnidad(item.getPrecioUnidad())
                                            .subtotal(item.getSubtotal())
                                            .build())
                                    .collect(Collectors.toList()) :
                            Collections.emptyList();

                    // Crear DTO
                    PedidoDisponibleDto pedidoDto = PedidoDisponibleDto.builder()
                            .id(pedido.getId())
                            .restauranteId(pedido.getRestauranteId())
                            .direccionEntregaId(pedido.getDireccionEntregaId())
                            .total(pedido.getTotal())
                            .estado(pedido.getEstado())
                            .fechaCreacion(pedido.getFechaCreacion())
                            .direccionEntrega(direccionCompleta)
                            .coordenadasEntrega(direccion.getCoordenadas())
                            .distanciaKm(distanciaKm)
                            .distanciaTexto(distancia.getDistance())
                            .tiempoEstimado(distancia.getDuration())
                            .items(items)
                            .build();

                    pedidosConDistancia.add(pedidoDto);

                } catch (Exception e) {
                    log.error("Error procesando pedido {}: {}", pedido.getId(), e.getMessage(), e);
                }
            }

            // 5. Ordenar por distancia (más cercanos primero)
            pedidosConDistancia.sort(Comparator.comparing(PedidoDisponibleDto::getDistanciaKm));

            log.info("Retornando {} pedidos disponibles dentro del rango", pedidosConDistancia.size());
            return pedidosConDistancia;
            
        } catch (RuntimeException e) {
            log.error("Error en obtenerPedidosDisponibles: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado en obtenerPedidosDisponibles: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener pedidos disponibles: " + e.getMessage(), e);
        }
    }

    /**
     * Aceptar un pedido (asignación manual)
     */
    public void aceptarPedido(UUID usuarioId, UUID pedidoId) {
        log.info("Repartidor {} aceptando pedido {}", usuarioId, pedidoId);

        // Verificar que el repartidor existe
        DeliveryUser deliveryUser = deliveryUserRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        // Obtener el pedido
        ResponseEntity<PedidoResponse> pedidoResponse = pedidoClient.obtenerPedido(pedidoId);
        PedidoResponse pedido = pedidoResponse.getBody();
        if (pedido == null) {
            throw new RuntimeException("Pedido no encontrado");
        }

        // Verificar que el pedido está disponible
        if (!"EN_COCINA".equals(pedido.getEstado())) {
            throw new RuntimeException("El pedido no está disponible para asignación");
        }

        if (pedido.getRepartidorId() != null) {
            throw new RuntimeException("El pedido ya está asignado a otro repartidor");
        }

        // Asignar repartidor al pedido (esto también cambia el estado a CON_EL_REPARTIDOR)
        pedidoClient.asignarRepartidor(pedidoId, deliveryUser.getId());

        log.info("Pedido {} aceptado por repartidor {}", pedidoId, usuarioId);
    }
}

