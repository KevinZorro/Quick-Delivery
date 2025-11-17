package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.client.PedidoClient;
import com.ufps.Quick_Delivery.dto.AceptarPedidoRequestDto;
import com.ufps.Quick_Delivery.dto.EntregaDto;
import com.ufps.Quick_Delivery.models.DeliveryUser;
import com.ufps.Quick_Delivery.models.Entrega;
import com.ufps.Quick_Delivery.models.EstadoEntrega;
import com.ufps.Quick_Delivery.models.PedidoNotificacion;
import com.ufps.Quick_Delivery.repository.DeliveryUserRepository;
import com.ufps.Quick_Delivery.repository.EntregaRepository;
import com.ufps.Quick_Delivery.repository.PedidoNotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final PedidoNotificacionRepository notificacionRepository;
    private final DeliveryUserRepository deliveryUserRepository;
    private final PedidoClient pedidoClient;

    /**
     * Aceptar un pedido y crear entrega
     */
    @Transactional
    public EntregaDto aceptarPedido(UUID usuarioId, UUID notificacionId, AceptarPedidoRequestDto request) {
        log.info("Repartidor {} aceptando pedido de notificación {}", usuarioId, notificacionId);

        // 1. Verificar que el repartidor existe
        DeliveryUser deliveryUser = deliveryUserRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        // 2. Obtener la notificación
        PedidoNotificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        // 3. Verificar que no esté procesada
        if (notificacion.getProcesado()) {
            throw new RuntimeException("Esta notificación ya fue procesada");
        }

        // 4. Verificar que no exista ya una entrega para este pedido
        Optional<Entrega> entregaExistente = entregaRepository.findByPedidoId(notificacion.getPedidoId());
        if (entregaExistente.isPresent()) {
            throw new RuntimeException("Ya existe una entrega para este pedido");
        }

        // 5. Generar código de entrega único
        String codigoEntrega = generarCodigoEntrega();

        // 6. Crear la entrega
        Entrega entrega = Entrega.builder()
                .clienteId(notificacion.getClienteId())
                .codigoEntrega(codigoEntrega)
                .comentario(request.getComentario())
                .estado(EstadoEntrega.EN_CAMINO_RECOGIDO)
                .pedidoId(notificacion.getPedidoId())
                .repartidorId(deliveryUser.getId())
                .fechaCreacion(LocalDateTime.now())
                .build();

        entrega = entregaRepository.save(entrega);

        // 7. Marcar notificación como procesada
        notificacion.setProcesado(true);
        notificacionRepository.save(notificacion);

        // 8. Asignar repartidor al pedido en el servicio Client
        try {
            pedidoClient.asignarRepartidor(notificacion.getPedidoId(), deliveryUser.getId());
            log.info("Repartidor asignado al pedido {} en el servicio Client", notificacion.getPedidoId());
        } catch (Exception e) {
            log.error("Error al asignar repartidor al pedido: {}", e.getMessage());
            // No lanzar excepción, la entrega ya está creada
        }

        log.info("Entrega creada con ID: {} y código: {}", entrega.getId(), codigoEntrega);
        return convertirADto(entrega);
    }

    /**
     * Listar entregas de un repartidor
     */
    @Transactional(readOnly = true)
    public List<EntregaDto> listarEntregasPorRepartidor(UUID usuarioId) {
        log.info("Listando entregas para repartidor: {}", usuarioId);

        DeliveryUser deliveryUser = deliveryUserRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        List<Entrega> entregas = entregaRepository.findByRepartidorIdOrderByFechaCreacionDesc(deliveryUser.getId());
        
        return entregas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar estado de una entrega
     */
    @Transactional
    public EntregaDto actualizarEstadoEntrega(UUID entregaId, EstadoEntrega nuevoEstado) {
        log.info("Actualizando estado de entrega {} a {}", entregaId, nuevoEstado);

        Entrega entrega = entregaRepository.findById(entregaId)
                .orElseThrow(() -> new RuntimeException("Entrega no encontrada"));

        entrega.setEstado(nuevoEstado);
        entrega.setFechaActualizacion(LocalDateTime.now());

        // Si el estado es ENTREGADO, también actualizar el pedido en Client
        if (nuevoEstado == EstadoEntrega.ENTREGADO) {
            try {
                pedidoClient.cambiarEstado(entrega.getPedidoId(), "ENTREGADO");
                log.info("Estado del pedido {} actualizado a ENTREGADO", entrega.getPedidoId());
            } catch (Exception e) {
                log.error("Error al actualizar estado del pedido: {}", e.getMessage());
            }
        }

        entrega = entregaRepository.save(entrega);
        return convertirADto(entrega);
    }

    private String generarCodigoEntrega() {
        // Generar código alfanumérico de 8 caracteres
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < 8; i++) {
            codigo.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        
        return codigo.toString();
    }

    private EntregaDto convertirADto(Entrega entrega) {
        return EntregaDto.builder()
                .id(entrega.getId())
                .clienteId(entrega.getClienteId())
                .codigoEntrega(entrega.getCodigoEntrega())
                .comentario(entrega.getComentario())
                .estado(entrega.getEstado())
                .pedidoId(entrega.getPedidoId())
                .repartidorId(entrega.getRepartidorId())
                .fechaCreacion(entrega.getFechaCreacion())
                .fechaActualizacion(entrega.getFechaActualizacion())
                .build();
    }
}

