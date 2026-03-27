package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.dto.ConfirmarEntregaDto;
import com.ufps.Quick_Delivery.dto.IniciarEntregaDto;
import com.ufps.Quick_Delivery.exceptions.EntregaException;
import com.ufps.Quick_Delivery.models.Entrega;
import com.ufps.Quick_Delivery.repository.EntregaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntregaService {

    private final EntregaRepository entregaRepository;

    public Entrega iniciarEntrega(IniciarEntregaDto dto) {

        entregaRepository.findByPedidoId(dto.getPedidoId()).ifPresent(e -> {
            throw new EntregaException("El pedido ya tiene una entrega iniciada.");
        });

        Entrega entrega = new Entrega();
        entrega.setPedidoId(dto.getPedidoId());
        entrega.setRepartidorId(dto.getRepartidorId());
        entrega.setEstado("CON_EL_REPARTIDOR");
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

        System.out.println("🔍 Entrega encontrada - ID: " + entrega.getId() + " - Estado actual: " + entrega.getEstado());

        if (entrega.getEstado().equals("ENTREGADO")) {
            throw new EntregaException("Esta entrega ya fue confirmada anteriormente");
        }

        if (!entrega.getCodigoConfirmacion().equals(dto.getCodigoEntrega())) {
            throw new EntregaException("Código de entrega incorrecto");
        }

        System.out.println("✅ Código correcto. Cambiando estado a ENTREGADO...");

        entrega.setEstado("ENTREGADO");
        entrega.setComentariosEntrega(dto.getComentarios());
        entrega.setHoraFin(LocalDateTime.now());

        long minutos = Duration.between(entrega.getHoraInicio(), entrega.getHoraFin()).toMinutes();
        entrega.setDuracionMinutos(minutos);

        Entrega entregaGuardada = entregaRepository.save(entrega);

        System.out.println("💾 Entrega guardada - ID: " + entregaGuardada.getId() +
                " - Estado guardado: " + entregaGuardada.getEstado() +
                " - Duración: " + minutos + " minutos");

        return entregaGuardada;
    }

    // ✅ solo acceso a datos
    public Optional<Entrega> obtenerPorPedidoId(UUID pedidoId) {
        return entregaRepository.findByPedidoId(pedidoId);
    }

    public Optional<Entrega> actualizarEstado(UUID entregaId, String nuevoEstado) {
        return entregaRepository.findById(entregaId).map(entrega -> {
            entrega.setEstado(nuevoEstado);
            return entregaRepository.save(entrega);
        });
    }
}