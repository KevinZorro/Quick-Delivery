package com.ufps.Quick_Delivery.DTO;

import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.model.ItemPedido;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class CarritoRequest {
    private Cliente cliente;
    private UUID restauranteId;
    private List<ItemPedido> items;
}
