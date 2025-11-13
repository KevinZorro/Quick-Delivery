package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.client.EdgeClient;
import com.ufps.Quick_Delivery.client.RestauranteClient;
import com.ufps.Quick_Delivery.dto.AceptarPedidoRequestDto;
import com.ufps.Quick_Delivery.dto.PedidoNotificacionConDistanciaDto;
import com.ufps.Quick_Delivery.dto.PedidoNotificacionRequestDto;
import com.ufps.Quick_Delivery.dto.PedidoNotificacionResponseDto;
import com.ufps.Quick_Delivery.dto.UbicacionRepartidorDto;
import com.ufps.Quick_Delivery.models.EstadoNotificacion;
import com.ufps.Quick_Delivery.models.PedidoNotificacion;
import com.ufps.Quick_Delivery.repository.PedidoNotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoNotificacionService {

    private final PedidoNotificacionRepository repository;
    private final EdgeClient edgeClient;
    private final RestauranteClient restauranteClient;

    /**
     * Convertir entidad a DTO
     */
    private PedidoNotificacionResponseDto toDto(PedidoNotificacion entity) {
        return PedidoNotificacionResponseDto.builder()
                .id(entity.getId())
                .pedidoId(entity.getPedidoId())
                .clienteId(entity.getClienteId())
                .restauranteId(entity.getRestauranteId())
                .direccionEntregaId(entity.getDireccionEntregaId())
                .estado(entity.getEstado())
                .repartidorId(entity.getRepartidorId())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaAceptacion(entity.getFechaAceptacion())
                .build();
    }

    /**
     * Crear una nueva notificación de pedido
     */
    @Transactional
    public PedidoNotificacionResponseDto crearNotificacion(PedidoNotificacionRequestDto request) {
        log.info("📦 Creando notificación de pedido: pedidoId={}, clienteId={}, restauranteId={}", 
                request.getPedidoId(), request.getClienteId(), request.getRestauranteId());

        // Verificar si ya existe una notificación para este pedido
        if (repository.existsByPedidoId(request.getPedidoId())) {
            log.warn("⚠️ Ya existe una notificación para el pedido: {}", request.getPedidoId());
            throw new RuntimeException("Ya existe una notificación para este pedido");
        }

        PedidoNotificacion notificacion = PedidoNotificacion.builder()
                .pedidoId(request.getPedidoId())
                .clienteId(request.getClienteId())
                .restauranteId(request.getRestauranteId())
                .direccionEntregaId(request.getDireccionEntregaId())
                .estado(EstadoNotificacion.PENDIENTE)
                .build();

        @SuppressWarnings("null")
        PedidoNotificacion saved = repository.save(notificacion);
        log.info("✅ Notificación creada exitosamente: id={}", saved.getId());
        
        return toDto(saved);
    }

    /**
     * Obtener todas las notificaciones pendientes (para que los repartidores las vean)
     */
    @Transactional(readOnly = true)
    public List<PedidoNotificacionResponseDto> obtenerNotificacionesPendientes() {
        log.info("🔍 Obteniendo notificaciones pendientes");
        List<PedidoNotificacion> notificaciones = repository.findByEstado(EstadoNotificacion.PENDIENTE);
        log.info("✅ Se encontraron {} notificaciones pendientes", notificaciones.size());
        return notificaciones.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener notificaciones pendientes filtradas por distancia desde la ubicación del repartidor
     * Solo muestra pedidos dentro del rango definido
     */
    @Transactional(readOnly = true)
    public List<PedidoNotificacionConDistanciaDto> obtenerNotificacionesPendientesPorUbicacion(
            UbicacionRepartidorDto ubicacion) {
        
        log.info("🔍 Obteniendo notificaciones pendientes para repartidor en ubicación: lat={}, lng={}, rango={}km", 
                ubicacion.getLatitud(), ubicacion.getLongitud(), ubicacion.getRangoMaximoKm());

        // Obtener todas las notificaciones pendientes
        List<PedidoNotificacion> notificaciones = repository.findByEstado(EstadoNotificacion.PENDIENTE);
        log.info("📦 Total de notificaciones pendientes encontradas: {}", notificaciones.size());

        List<PedidoNotificacionConDistanciaDto> notificacionesConDistancia = new ArrayList<>();
        double rangoMaximoMetros = ubicacion.getRangoMaximoKm() * 1000; // Convertir km a metros

        for (PedidoNotificacion notificacion : notificaciones) {
            try {
                // 1. Obtener información del restaurante para obtener su usuarioId
                RestauranteClient.RestauranteResponse restaurante = restauranteClient.obtenerRestaurante(
                        notificacion.getRestauranteId());
                
                if (restaurante == null || restaurante.getUsuarioId() == null) {
                    log.warn("⚠️ No se pudo obtener información del restaurante {}", notificacion.getRestauranteId());
                    continue;
                }

                // 2. Obtener direcciones del restaurante desde Edge usando su usuarioId
                // Necesitamos obtener la dirección principal del restaurante
                // Por ahora, usaremos la primera dirección disponible del restaurante
                // En producción, deberías tener un endpoint específico para obtener la dirección principal
                List<EdgeClient.DireccionResponse> direccionesRestaurante = edgeClient.obtenerDireccionesPorUsuario(
                        restaurante.getUsuarioId());

                if (direccionesRestaurante == null || direccionesRestaurante.isEmpty()) {
                    log.warn("⚠️ El restaurante {} no tiene direcciones registradas", restaurante.getId());
                    continue;
                }

                // Usar la primera dirección del restaurante (idealmente debería ser la principal)
                EdgeClient.DireccionResponse direccionRestaurante = direccionesRestaurante.get(0);
                
                if (direccionRestaurante.getCoordenadas() == null || direccionRestaurante.getCoordenadas().isEmpty()) {
                    log.warn("⚠️ Dirección del restaurante {} no tiene coordenadas, omitiendo pedido {}", 
                            direccionRestaurante.getId(), notificacion.getPedidoId());
                    continue;
                }

                // Parsear coordenadas del restaurante (formato: "lat,lng")
                String[] coords = direccionRestaurante.getCoordenadas().split(",");
                if (coords.length != 2) {
                    log.warn("⚠️ Formato de coordenadas inválido: {}", direccionRestaurante.getCoordenadas());
                    continue;
                }

                String restauranteLat = coords[0].trim();
                String restauranteLng = coords[1].trim();

                // Calcular distancia usando Google Maps API
                EdgeClient.DistanceMatrixRequest distanceRequest = new EdgeClient.DistanceMatrixRequest();
                distanceRequest.setOriginLat(String.valueOf(ubicacion.getLatitud()));
                distanceRequest.setOriginLng(String.valueOf(ubicacion.getLongitud()));
                distanceRequest.setDestinationLat(restauranteLat);
                distanceRequest.setDestinationLng(restauranteLng);

                EdgeClient.DistanceMatrixResponse distanceResponse = edgeClient.calcularDistancia(distanceRequest);

                if (distanceResponse == null || distanceResponse.getDistanceValue() == null) {
                    log.warn("⚠️ No se pudo calcular la distancia para el pedido {}", notificacion.getPedidoId());
                    continue;
                }

                long distanciaMetros = distanceResponse.getDistanceValue();
                double distanciaKm = distanciaMetros / 1000.0;

                // Filtrar por rango máximo
                if (distanciaMetros <= rangoMaximoMetros) {
                    PedidoNotificacionConDistanciaDto dto = PedidoNotificacionConDistanciaDto.builder()
                            .id(notificacion.getId())
                            .pedidoId(notificacion.getPedidoId())
                            .clienteId(notificacion.getClienteId())
                            .restauranteId(notificacion.getRestauranteId())
                            .direccionEntregaId(notificacion.getDireccionEntregaId())
                            .estado(notificacion.getEstado())
                            .repartidorId(notificacion.getRepartidorId())
                            .fechaCreacion(notificacion.getFechaCreacion())
                            .fechaAceptacion(notificacion.getFechaAceptacion())
                            .distanciaKm(distanciaKm)
                            .distanciaMetros(distanciaMetros)
                            .distanciaFormateada(distanceResponse.getDistance())
                            .duracionEstimada(distanceResponse.getDuration())
                            .build();

                    notificacionesConDistancia.add(dto);
                    log.info("✅ Pedido {} está a {:.2f} km (dentro del rango)", 
                            notificacion.getPedidoId(), distanciaKm);
                } else {
                    log.debug("⏭️ Pedido {} está a {:.2f} km (fuera del rango de {} km)", 
                            notificacion.getPedidoId(), distanciaKm, ubicacion.getRangoMaximoKm());
                }

            } catch (Exception e) {
                log.error("❌ Error al procesar notificación {}: {}", 
                        notificacion.getId(), e.getMessage(), e);
                // Continuar con la siguiente notificación
            }
        }

        log.info("✅ Se encontraron {} pedidos dentro del rango de {} km", 
                notificacionesConDistancia.size(), ubicacion.getRangoMaximoKm());

        return notificacionesConDistancia;
    }

    /**
     * Obtener una notificación por ID de pedido
     */
    @Transactional(readOnly = true)
    public Optional<PedidoNotificacionResponseDto> obtenerPorPedidoId(UUID pedidoId) {
        return repository.findByPedidoId(pedidoId)
                .map(this::toDto);
    }

    /**
     * Aceptar un pedido por un repartidor
     * Solo puede ser aceptado si está en estado PENDIENTE
     */
    @Transactional
    public PedidoNotificacionResponseDto aceptarPedido(UUID notificacionId, AceptarPedidoRequestDto request) {
        log.info("🚚 Repartidor {} intentando aceptar notificación {}", request.getRepartidorId(), notificacionId);

        PedidoNotificacion notificacion = repository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        // Verificar que la notificación esté pendiente
        if (notificacion.getEstado() != EstadoNotificacion.PENDIENTE) {
            log.warn("⚠️ La notificación {} ya no está pendiente. Estado actual: {}", 
                    notificacionId, notificacion.getEstado());
            throw new RuntimeException("Este pedido ya fue aceptado o rechazado");
        }

        // Verificar que no haya sido aceptado por otro repartidor (doble verificación)
        if (notificacion.getRepartidorId() != null) {
            log.warn("⚠️ El pedido {} ya fue aceptado por otro repartidor", notificacion.getPedidoId());
            throw new RuntimeException("Este pedido ya fue aceptado por otro repartidor");
        }

        // Asignar el repartidor y cambiar el estado
        notificacion.setRepartidorId(request.getRepartidorId());
        notificacion.setEstado(EstadoNotificacion.ACEPTADO);

        PedidoNotificacion actualizada = repository.save(notificacion);
        log.info("✅ Pedido {} aceptado exitosamente por repartidor {}", 
                actualizada.getPedidoId(), request.getRepartidorId());

        return toDto(actualizada);
    }

    /**
     * Obtener pedidos aceptados por un repartidor
     */
    @Transactional(readOnly = true)
    public List<PedidoNotificacionResponseDto> obtenerPedidosAceptadosPorRepartidor(UUID repartidorId) {
        log.info("🔍 Obteniendo pedidos aceptados por repartidor: {}", repartidorId);
        List<PedidoNotificacion> notificaciones = repository.findByRepartidorIdAndEstado(
                repartidorId, EstadoNotificacion.ACEPTADO);
        return notificaciones.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener todas las notificaciones
     */
    @Transactional(readOnly = true)
    public List<PedidoNotificacionResponseDto> obtenerTodas() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

