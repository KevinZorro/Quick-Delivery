package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.dto.ConfirmarEntregaRequest;
import com.ufps.Quick_Delivery.models.Entrega;
import com.ufps.Quick_Delivery.repository.EntregaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final RestauranteClient restauranteClient;

    public String confirmarEntrega(UUID repartidorId, ConfirmarEntregaRequest request) {
        var entregas = entregaRepository.findByPedidoId(request.getPedidoId());
    
        if (entregas.isEmpty()) {
            return "❌ No se encontró una entrega asociada a este pedido.";
        }
    
        Entrega entrega = entregas.stream()
                .filter(e -> "EN_CAMINO".equalsIgnoreCase(e.getEstado()) && e.getRepartidorId().equals(repartidorId))
                .findFirst()
                .orElse(null);
    
        if (entrega == null) {
            return "⚠️ No se encontró una entrega EN_CAMINO asignada a este repartidor.";
        }
    
        if (!entrega.getCodigoEntrega().equals(request.getCodigoEntrega())) {
            return "❌ Código de entrega incorrecto.";
        }
    
        entrega.setEstado("ENTREGADO");
        entrega.setComentarios(request.getComentarios());
        entregaRepository.save(entrega);
    
        restauranteClient.actualizarEstadoPedido(request.getPedidoId(), "ENTREGADO");
    
        return "✅ Entrega confirmada correctamente.";
    }
    

    public Entrega guardarEntrega(Entrega entrega) {
        return entregaRepository.save(entrega);
    }

    public List<Entrega> obtenerTodas() {
        return entregaRepository.findAll();
    }

    public void eliminarEntrega(UUID id) {
        try {
            log.info(" Intentando eliminar entrega con ID: {}", id);

            if (!entregaRepository.existsById(id)) {
                log.warn(" No se encontró la entrega con ID: {}", id);
                throw new RuntimeException("Entrega no encontrada con ID: " + id);
            }

            entregaRepository.deleteById(id);
            log.info(" Entrega eliminada correctamente: {}", id);

        } catch (Exception e) {
            log.error(" Error al eliminar entrega con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar entrega: " + e.getMessage());
        }
    }    
}
