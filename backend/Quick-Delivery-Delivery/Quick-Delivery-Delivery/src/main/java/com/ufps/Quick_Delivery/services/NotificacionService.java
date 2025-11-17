package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.client.*;
import com.ufps.Quick_Delivery.dto.NotificacionPedidoDto;
import com.ufps.Quick_Delivery.dto.PedidoNotificacionRequestDto;
import com.ufps.Quick_Delivery.models.DeliveryUser;
import com.ufps.Quick_Delivery.models.PedidoNotificacion;
import com.ufps.Quick_Delivery.repository.DeliveryUserRepository;
import com.ufps.Quick_Delivery.repository.PedidoNotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final PedidoNotificacionRepository notificacionRepository;
    private final DeliveryUserRepository deliveryUserRepository;
    private final RestauranteServiceClient restauranteServiceClient;
    private final DireccionClient direccionClient;
    private final GoogleMapsClient googleMapsClient;

    /**
     * Recibir notificación de nuevo pedido desde Client
     */
    @Transactional
    public PedidoNotificacion recibirNotificacionPedido(PedidoNotificacionRequestDto request) {
        log.info("Recibiendo notificación de pedido: {}", request.getPedidoId());

        // Verificar si ya existe
        Optional<PedidoNotificacion> existente = notificacionRepository.findByPedidoId(request.getPedidoId());
        if (existente.isPresent()) {
            log.warn("Notificación para pedido {} ya existe", request.getPedidoId());
            return existente.get();
        }

        PedidoNotificacion notificacion = PedidoNotificacion.builder()
                .pedidoId(request.getPedidoId())
                .restauranteId(request.getRestauranteId())
                .clienteId(request.getClienteId())
                .total(request.getTotal())
                .fechaCreacion(LocalDateTime.now())
                .procesado(false)
                .build();

        return notificacionRepository.save(notificacion);
    }

    /**
     * Obtener notificaciones disponibles para un repartidor según su ubicación y rango
     */
    @Transactional(readOnly = true)
    public List<NotificacionPedidoDto> obtenerNotificacionesDisponibles(UUID usuarioId) {
        log.info("Obteniendo notificaciones disponibles para repartidor: {}", usuarioId);

        // 1. Obtener el delivery user
        DeliveryUser deliveryUser = deliveryUserRepository.findByUsuarioId(usuarioId)
                .orElse(null);

        if (deliveryUser == null || deliveryUser.getLatitud() == null || deliveryUser.getLongitud() == null) {
            log.warn("Repartidor {} no tiene ubicación GPS configurada", usuarioId);
            return Collections.emptyList();
        }

        Double rangoKm = deliveryUser.getRangoKm() != null ? deliveryUser.getRangoKm() : 10.0;
        log.info("Rango configurado: {} km", rangoKm);

        // 2. Obtener notificaciones no procesadas
        List<PedidoNotificacion> notificaciones = notificacionRepository.findByProcesadoFalseOrderByFechaCreacionDesc();
        log.info("Encontradas {} notificaciones sin procesar", notificaciones.size());

        List<NotificacionPedidoDto> notificacionesDisponibles = new ArrayList<>();

        for (PedidoNotificacion notificacion : notificaciones) {
            try {
                // 3. Obtener restaurante para obtener usuarioId
                ResponseEntity<RestauranteResponse> restauranteResponse = restauranteServiceClient.obtenerRestaurante(
                        notificacion.getRestauranteId()
                );

                if (restauranteResponse.getBody() == null) {
                    log.warn("Restaurante {} no encontrado", notificacion.getRestauranteId());
                    continue;
                }

                RestauranteResponse restaurante = restauranteResponse.getBody();
                UUID restauranteUsuarioId = restaurante.getUsuarioId();

                // 4. Obtener direcciones del restaurante
                ResponseEntity<List<DireccionResponse>> direccionesResponse = direccionClient.obtenerDireccionesPorUsuario(restauranteUsuarioId);
                
                if (direccionesResponse.getBody() == null || direccionesResponse.getBody().isEmpty()) {
                    log.warn("Restaurante {} no tiene direcciones", notificacion.getRestauranteId());
                    continue;
                }

                // Obtener la primera dirección con coordenadas
                DireccionResponse direccionRestaurante = direccionesResponse.getBody().stream()
                        .filter(d -> d.getCoordenadas() != null && !d.getCoordenadas().trim().isEmpty())
                        .findFirst()
                        .orElse(null);

                if (direccionRestaurante == null) {
                    log.warn("Restaurante {} no tiene coordenadas", notificacion.getRestauranteId());
                    continue;
                }

                // 5. Calcular distancia entre repartidor y restaurante
                String[] coordenadas = direccionRestaurante.getCoordenadas().split(",");
                if (coordenadas.length != 2) {
                    log.warn("Coordenadas inválidas para restaurante: {}", direccionRestaurante.getCoordenadas());
                    continue;
                }

                String restLat = coordenadas[0].trim();
                String restLng = coordenadas[1].trim();

                DistanceMatrixRequest distanceRequest = new DistanceMatrixRequest();
                distanceRequest.setOriginLat(deliveryUser.getLatitud().toString());
                distanceRequest.setOriginLng(deliveryUser.getLongitud().toString());
                distanceRequest.setDestinationLat(restLat);
                distanceRequest.setDestinationLng(restLng);

                ResponseEntity<DistanceMatrixResponse> distanceResponse = googleMapsClient.calcularDistancia(distanceRequest);
                DistanceMatrixResponse distancia = distanceResponse.getBody();

                if (distancia == null || distancia.getDistanceValue() == null) {
                    log.warn("No se pudo calcular distancia para notificación {}", notificacion.getId());
                    continue;
                }

                double distanciaKm = distancia.getDistanceValue() / 1000.0;

                // 6. Filtrar por rango
                if (distanciaKm > rangoKm) {
                    log.debug("Notificación {} está fuera del rango ({} km > {} km)", notificacion.getId(), distanciaKm, rangoKm);
                    continue;
                }

                // 7. Construir dirección completa del restaurante
                String direccionCompleta = String.format("%s, %s, %s",
                        direccionRestaurante.getCalle(),
                        direccionRestaurante.getBarrio(),
                        direccionRestaurante.getCiudad());

                // 8. Crear DTO
                NotificacionPedidoDto dto = NotificacionPedidoDto.builder()
                        .id(notificacion.getId())
                        .pedidoId(notificacion.getPedidoId())
                        .restauranteId(notificacion.getRestauranteId())
                        .clienteId(notificacion.getClienteId())
                        .total(notificacion.getTotal())
                        .fechaCreacion(notificacion.getFechaCreacion())
                        .direccionRestaurante(direccionCompleta)
                        .coordenadasRestaurante(direccionRestaurante.getCoordenadas())
                        .distanciaKm(distanciaKm)
                        .distanciaTexto(distancia.getDistance())
                        .tiempoEstimado(distancia.getDuration())
                        .build();

                notificacionesDisponibles.add(dto);

            } catch (Exception e) {
                log.error("Error procesando notificación {}: {}", notificacion.getId(), e.getMessage(), e);
            }
        }

        // Ordenar por distancia (más cercanos primero)
        notificacionesDisponibles.sort(Comparator.comparing(NotificacionPedidoDto::getDistanciaKm));

        log.info("Retornando {} notificaciones disponibles dentro del rango", notificacionesDisponibles.size());
        return notificacionesDisponibles;
    }
}

