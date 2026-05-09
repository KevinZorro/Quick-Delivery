package com.ufps.Quick_Delivery.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ufps.Quick_Delivery.dto.NotificacionEstadoPedidoDto;
import com.ufps.Quick_Delivery.model.Pedido;

@Service
public class NotificacionService {

    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;

    private final Map<UUID, List<SseEmitter>> clientesConectados = new ConcurrentHashMap<>();

    public SseEmitter suscribirCliente(UUID usuarioId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        clientesConectados.computeIfAbsent(usuarioId, key -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removerCliente(usuarioId, emitter));
        emitter.onTimeout(() -> removerCliente(usuarioId, emitter));
        emitter.onError(error -> removerCliente(usuarioId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("conectado")
                    .data(Map.of("mensaje", "Conectado a notificaciones de pedidos")));
        } catch (IOException e) {
            removerCliente(usuarioId, emitter);
        }

        return emitter;
    }

    public void notificarCambioEstado(Pedido pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().getUsuarioId() == null) {
            return;
        }

        UUID usuarioId = pedido.getCliente().getUsuarioId();
        NotificacionEstadoPedidoDto notificacion = NotificacionEstadoPedidoDto.builder()
                .pedidoId(pedido.getId())
                .clienteId(pedido.getCliente().getId())
                .usuarioId(usuarioId)
                .restauranteId(pedido.getRestauranteId())
                .estado(pedido.getEstado().name())
                .titulo("Tu pedido cambio de estado")
                .mensaje("Pedido #" + pedido.getId().toString().substring(0, 8)
                        + ": " + textoEstado(pedido.getEstado().name()))
                .fecha(LocalDateTime.now())
                .build();

        enviarACliente(usuarioId, notificacion);
    }

    private void enviarACliente(UUID usuarioId, NotificacionEstadoPedidoDto notificacion) {
        List<SseEmitter> emitters = clientesConectados.get(usuarioId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("estado-pedido")
                        .data(notificacion));
            } catch (IOException | IllegalStateException e) {
                removerCliente(usuarioId, emitter);
            }
        }
    }

    private void removerCliente(UUID usuarioId, SseEmitter emitter) {
        List<SseEmitter> emitters = clientesConectados.get(usuarioId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                clientesConectados.remove(usuarioId);
            }
        }
    }

    private String textoEstado(String estado) {
        return switch (estado) {
            case "NUEVO" -> "es nuevo y esta esperando confirmacion del restaurante";
            case "ACEPTADO" -> "fue aceptado por el restaurante";
            case "EN_COCINA" -> "esta en cocina";
            case "CON_EL_REPARTIDOR" -> "va con el repartidor";
            case "ENTREGADO" -> "fue entregado";
            case "RECHAZADO_POR_RESTAURANTE" -> "fue rechazado por el restaurante";
            case "INICIADO" -> "recibido por el restaurante"; // Retrocompatibilidad
            default -> estado;
        };
    }
}
