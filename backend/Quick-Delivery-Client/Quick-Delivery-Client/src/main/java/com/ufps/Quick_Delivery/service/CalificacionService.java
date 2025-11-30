
package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.DeliveryFeignClient;
import com.ufps.Quick_Delivery.dto.NuevaCalificacionRepartidorDto;
import com.ufps.Quick_Delivery.model.EstadoPedido;
import com.ufps.Quick_Delivery.model.Pedido;
import com.ufps.Quick_Delivery.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalificacionService {

    private final PedidoRepository pedidoRepo;
    private final DeliveryFeignClient deliveryFeignClient;

    @Transactional
    public void calificarRepartidor(UUID pedidoId, int calificacion, String comentario) {
        if (calificacion < 1 || calificacion > 5) {
            throw new IllegalArgumentException("Calificación debe ser de 1 a 5 estrellas");
        }
        Pedido pedido = pedidoRepo.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no existe"));

        // Verifica estado entregado
        if (!pedido.getEstado().equals(EstadoPedido.ENTREGADO)) {
            throw new RuntimeException("Solo pedidos entregados pueden ser calificados");
        }

        NuevaCalificacionRepartidorDto dto = new NuevaCalificacionRepartidorDto();
        dto.setPedidoId(pedidoId);
        dto.setClienteId(pedido.getCliente().getId());
        dto.setCalificacion(calificacion);
        dto.setComentario(comentario);

        deliveryFeignClient.enviarCalificacion(pedido.getRepartidorId(), dto);
    }
}
