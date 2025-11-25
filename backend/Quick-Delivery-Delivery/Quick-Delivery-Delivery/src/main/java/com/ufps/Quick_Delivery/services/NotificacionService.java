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

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final PedidoNotificacionRepository notificacionRepository;
    private final DeliveryUserRepository deliveryUserRepository;
    private final RestauranteServiceClient restauranteServiceClient;
    private final DireccionClient direccionClient;
    private final GoogleMapsClient googleMapsClient;

        public List<PedidoNotificacion> obtenerTodas() {
        return notificacionRepository.findAll();
    }

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

    // 1. Obtener el delivery user local para configuración (rango)
    DeliveryUser deliveryUser = deliveryUserRepository.findByUsuarioId(usuarioId).orElse(null);

    if (deliveryUser == null) {
        log.warn("Repartidor {} no encontrado", usuarioId);
        return Collections.emptyList();
    }

    Double rangoKm = deliveryUser.getRangoKm() != null ? deliveryUser.getRangoKm() : 20.0;
    log.info("Rango configurado: {} km", rangoKm);

    // 2. Obtener direcciones del repartidor desde microservicio direcciones
    ResponseEntity<List<DireccionResponse>> direccionResponse = direccionClient.obtenerDireccionesPorUsuario(usuarioId);

    System.out.println("Respuesta de direcciones: " + direccionResponse);
    if (direccionResponse.getBody() == null || direccionResponse.getBody().isEmpty()) {
        log.warn("Repartidor {} no tiene direcciones registradas", usuarioId);
        return Collections.emptyList();
    }

    DireccionResponse direccionRepartidor = direccionResponse.getBody().stream()
        .filter(d -> d.getCoordenadas() != null && !d.getCoordenadas().trim().isEmpty())
        .findFirst()
        .orElse(null);

    if (direccionRepartidor == null) {
        log.warn("Repartidor {} no tiene coordenadas válidas en sus direcciones", usuarioId);
        return Collections.emptyList();
    }

    String[] coords = direccionRepartidor.getCoordenadas().split(",");
    if (coords.length != 2) {
        log.warn("Coordenadas inválidas para repartidor {}: {}", usuarioId, direccionRepartidor.getCoordenadas());
        return Collections.emptyList();
    }

    String repartidorLat = coords[0].trim();
    String repartidorLng = coords[1].trim();

    // 3. Obtener notificaciones sin procesar
    List<PedidoNotificacion> notificaciones = this.obtenerTodas();
    log.info("Encontradas {} notificaciones sin procesar", notificaciones.size());

    List<NotificacionPedidoDto> notificacionesDisponibles = new ArrayList<>();

    for (PedidoNotificacion notificacion : notificaciones) {
        try {
            // Obtener restaurante para obtener usuarioId
            ResponseEntity<RestauranteResponse> restauranteResponse = restauranteServiceClient.obtenerRestaurante(notificacion.getRestauranteId());
            RestauranteResponse restaurante = restauranteResponse.getBody();
            if (restaurante == null) {
                log.warn("Restaurante {} no encontrado", notificacion.getRestauranteId());
                continue;
            }

            UUID restauranteUsuarioId = restaurante.getUsuarioId();

            // Obtener direcciones del restaurante
            ResponseEntity<List<DireccionResponse>> direccionesRestauranteResponse = direccionClient.obtenerDireccionesPorUsuario(restauranteUsuarioId);
            List<DireccionResponse> direccionesRestaurante = direccionesRestauranteResponse.getBody();
            if (direccionesRestaurante == null || direccionesRestaurante.isEmpty()) {
                log.warn("Restaurante {} no tiene direcciones", notificacion.getRestauranteId());
                continue;
            }

            // Usar primera dirección válida con coordenadas
            DireccionResponse direccionRestaurante = direccionesRestaurante.stream()
                .filter(d -> d.getCoordenadas() != null && !d.getCoordenadas().trim().isEmpty())
                .findFirst()
                .orElse(null);

            if (direccionRestaurante == null) {
                log.warn("Restaurante {} no tiene coordenadas válidas", notificacion.getRestauranteId());
                continue;
            }

            String[] coordsRest = direccionRestaurante.getCoordenadas().split(",");
            if (coordsRest.length != 2) {
                log.warn("Coordenadas inválidas para restaurante {}: {}", notificacion.getRestauranteId(), direccionRestaurante.getCoordenadas());
                continue;
            }

            String restLat = coordsRest[0].trim();
            String restLng = coordsRest[1].trim();

            // Calcular distancia entre repartidor y restaurante usando Google Maps Client
            DistanceMatrixRequest distanceRequest = new DistanceMatrixRequest();
            distanceRequest.setOriginLat(repartidorLat);
            distanceRequest.setOriginLng(repartidorLng);
            distanceRequest.setDestinationLat(restLat);
            distanceRequest.setDestinationLng(restLng);

            ResponseEntity<DistanceMatrixResponse> distanceResponse = googleMapsClient.calcularDistancia(distanceRequest);
            DistanceMatrixResponse distancia = distanceResponse.getBody();
            System.out.println("Distancia calculada: " + distancia);

            if (distancia == null || distancia.getDistanceValue() == null) {
                log.warn("No se pudo calcular distancia para notificación {}", notificacion.getId());
                continue;
            }

            double distanciaKm = distancia.getDistanceValue() / 1000.0;

            log.info("Distancia a notificación {}: {} km (rango permitido: {} km)", notificacion.getId(), distanciaKm, rangoKm);

            // Filtrar por rango
            if (distanciaKm > rangoKm) {
                log.debug("Notificación {} está fuera del rango ({} km > {} km)", notificacion.getId(), distanciaKm, rangoKm);
                continue;
            }

            // Construir DTO con datos de notificación y distancia
            String direccionCompleta = String.format("%s, %s, %s",
                direccionRestaurante.getCalle(),
                direccionRestaurante.getBarrio(),
                direccionRestaurante.getCiudad());

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

    // Ordenar por distancia de menor a mayor
    notificacionesDisponibles.sort(Comparator.comparing(NotificacionPedidoDto::getDistanciaKm));

    log.info("Retornando {} notificaciones disponibles dentro del rango", notificacionesDisponibles.size());
    return notificacionesDisponibles;
}

}

