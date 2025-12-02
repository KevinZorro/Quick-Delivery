package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.model.Pedido;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    public void notificarCambioEstado(Pedido pedido) {
        // Notificar al cliente (aquí solo log)
        System.out.println("📩 Notificando al cliente (clienteId="
                + (pedido.getCliente() != null ? pedido.getCliente().getId() : "null")
                + ") sobre cambio de estado: " + pedido.getEstado());

        // Si tienes solo repartidorId en el modelo:
        if (pedido.getRepartidorId() != null) {
            System.out.println("📦 Notificando al repartidor (repartidorId="
                    + pedido.getRepartidorId() + ")");
        }
    }
}
