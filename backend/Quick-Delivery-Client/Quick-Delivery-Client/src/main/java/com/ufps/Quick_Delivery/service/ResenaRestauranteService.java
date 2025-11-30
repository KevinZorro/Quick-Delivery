package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.RestauranteFeignClient;
import com.ufps.Quick_Delivery.dto.NuevaResenaRestauranteDto;
import com.ufps.Quick_Delivery.model.EstadoPedido;
import com.ufps.Quick_Delivery.model.Pedido;
import com.ufps.Quick_Delivery.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResenaRestauranteService {

    private final PedidoRepository pedidoRepo;
    private final RestauranteFeignClient restauranteFeignClient;

    @Transactional
    public void calificarRestaurante(UUID pedidoId, int calificacion, String comentario) {
        if (calificacion < 1 || calificacion > 5) {
            throw new IllegalArgumentException("La calificación debe ser un valor entre 1 y 5 estrellas.");
        }

        Pedido pedido = pedidoRepo.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no existe"));

        if (!pedido.getEstado().equals(EstadoPedido.ENTREGADO)) {
            throw new RuntimeException("Solo pedidos entregados pueden calificar restaurante");
        }

        NuevaResenaRestauranteDto dto = new NuevaResenaRestauranteDto();
        dto.setPedidoId(pedidoId);
        dto.setClienteId(pedido.getCliente().getId());
        dto.setCalificacion(calificacion);
        dto.setComentario(comentario);

        // Envía la reseña mediante Feign (se recomienda que el método en el Feign tenga el nombre crearResenaRestaurante)
        restauranteFeignClient.crearResenaRestaurante(pedido.getRestauranteId(), dto);
    }
}
