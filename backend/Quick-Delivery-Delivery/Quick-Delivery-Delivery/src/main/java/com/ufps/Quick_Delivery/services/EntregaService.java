package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.dto.ConfirmarEntregaRequest;
import com.ufps.Quick_Delivery.models.Entrega;
import com.ufps.Quick_Delivery.repository.EntregaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final RestauranteClient restauranteClient;

    public String confirmarEntrega(UUID repartidorId, ConfirmarEntregaRequest request) {

        Optional<Entrega> entregaOpt = entregaRepository.findByPedidoId(request.getPedidoId());

        if (entregaOpt.isEmpty()) {
            return "❌ No se encontró una entrega asociada a este pedido.";
        }

        Entrega entrega = entregaOpt.get();

        if (!"EN_CAMINO".equalsIgnoreCase(entrega.getEstado())) {
            return "⚠️ Solo se pueden confirmar entregas que estén en estado 'EN_CAMINO'.";
        }

        if (!entrega.getCodigoEntrega().equals(request.getCodigoEntrega())) {
            return "❌ Código de entrega incorrecto.";
        }

        entrega.setEstado("ENTREGADO");
        entrega.setComentarios(request.getComentarios());
        entregaRepository.save(entrega);

        // Comunicación con Restaurante
        restauranteClient.actualizarEstadoPedido(request.getPedidoId(), "ENTREGADO");

        return "✅ Entrega confirmada correctamente.";
    }
}
