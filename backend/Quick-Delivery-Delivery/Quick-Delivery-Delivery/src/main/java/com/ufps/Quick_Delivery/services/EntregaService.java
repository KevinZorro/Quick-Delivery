package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.dto.ConfirmarEntregaDto;
import com.ufps.Quick_Delivery.dto.IniciarEntregaDto;
import com.ufps.Quick_Delivery.dto.RegistrarTiempoEntregaDto;
import com.ufps.Quick_Delivery.exceptions.EntregaException;
import com.ufps.Quick_Delivery.models.Entrega;
import com.ufps.Quick_Delivery.repository.EntregaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntregaService {

    private final EntregaRepository entregaRepository;

    // INICIAR ENTREGA
    public Entrega iniciarEntrega(IniciarEntregaDto dto) {

        entregaRepository.findByPedidoId(dto.getPedidoId()).ifPresent(e -> {
            throw new EntregaException("El pedido ya tiene una entrega iniciada.");
        });

        Entrega entrega = new Entrega();
        entrega.setPedidoId(dto.getPedidoId());
        entrega.setRepartidorId(dto.getRepartidorId());
        entrega.setEstado("EN_CAMINO");
        entrega.setHoraInicio(LocalDateTime.now());
        entrega.setCodigoConfirmacion(generarCodigo());

        return entregaRepository.save(entrega);
    }

    private String generarCodigo() {
        return "ENT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public Entrega confirmarEntrega(ConfirmarEntregaDto dto) {

        Entrega entrega = entregaRepository.findByPedidoId(dto.getPedidoId())
                .orElseThrow(() -> new EntregaException("Entrega no encontrada"));

        if (!entrega.getEstado().equals("EN_CAMINO"))
            throw new EntregaException("Solo se pueden confirmar entregas en estado 'EN_CAMINO'");

        if (!entrega.getCodigoConfirmacion().equals(dto.getCodigoEntrega()))
            throw new EntregaException("Código de entrega incorrecto");

        // Cambiar estado
        entrega.setEstado("ENTREGADO");

        // Guardar comentarios
        entrega.setComentariosEntrega(dto.getComentarios());

        // Registrar hora fin
        entrega.setHoraFin(LocalDateTime.now());

        // Calcular duración
        long minutos = Duration.between(entrega.getHoraInicio(), entrega.getHoraFin()).toMinutes();
        entrega.setDuracionMinutos(minutos);

        return entregaRepository.save(entrega);
    }
}
