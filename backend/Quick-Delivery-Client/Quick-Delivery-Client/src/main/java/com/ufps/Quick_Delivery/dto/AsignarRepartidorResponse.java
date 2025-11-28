package com.ufps.Quick_Delivery.DTO;

import com.ufps.Quick_Delivery.model.Pedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarRepartidorResponse {

    private Pedido pedido;
    private String codigoEntrega;
}
