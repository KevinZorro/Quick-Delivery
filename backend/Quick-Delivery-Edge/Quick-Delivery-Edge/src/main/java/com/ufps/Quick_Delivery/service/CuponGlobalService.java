package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.ClientePedidoClient;
import com.ufps.Quick_Delivery.dto.AplicarCuponRequest;
import com.ufps.Quick_Delivery.dto.CuponGlobalDto;
import com.ufps.Quick_Delivery.model.CuponGlobal;
import com.ufps.Quick_Delivery.model.TipoCupon;
import com.ufps.Quick_Delivery.model.UsoCuponGlobal;
import com.ufps.Quick_Delivery.repository.CuponGlobalRepository;
import com.ufps.Quick_Delivery.repository.UsoCuponGlobalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuponGlobalService {

    private final CuponGlobalRepository cuponGlobalRepository;
    private final UsoCuponGlobalRepository usoCuponGlobalRepository;
    private final ClientePedidoClient clientePedidoClient;

    // Retorna cupones activos indicando si el cliente puede usar cada uno
    public List<CuponGlobalDto> listarDisponibles(UUID clienteId) {
        return cuponGlobalRepository.findByActivoTrue().stream()
                .filter(c -> !estaExpirado(c))
                .filter(c -> !estaAgotado(c))
                .map(c -> toDto(c, clienteId))
                .collect(Collectors.toList());
    }

    // Lista todos los cupones sin importar estado (para admin)
    public List<CuponGlobal> listarTodos() {
        return cuponGlobalRepository.findAll();
    }

    // Crea un nuevo cupón global
    public CuponGlobal crear(CuponGlobal cupon) {
        cupon.setActivo(true);
        cupon.setUsosActuales(0);
        return cuponGlobalRepository.save(cupon);
    }

    // Elimina un cupón
    public void eliminar(UUID id) {
        cuponGlobalRepository.deleteById(id);
    }

    // Aplica el cupón: valida y registra el uso vinculado al pedido
    @Transactional
    public void aplicar(AplicarCuponRequest request) {
        CuponGlobal cupon = cuponGlobalRepository.findById(request.cuponId())
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado"));

        if (!cupon.isActivo()) {
            throw new RuntimeException("El cupón no está activo");
        }
        if (estaExpirado(cupon)) {
            throw new RuntimeException("El cupón ha expirado");
        }
        if (estaAgotado(cupon)) {
            throw new RuntimeException("El cupón ya fue agotado");
        }
        if (usoCuponGlobalRepository.existsByCuponIdAndClienteId(cupon.getId(), request.clienteId())) {
            throw new RuntimeException("Ya usaste este cupón");
        }

        // Validación especial para PRIMERA_COMPRA: solo si es su primer pedido
        // (la verificación real se delega al frontend/cliente; aquí validamos solo la regla del cupón)

        // Registrar uso
        UsoCuponGlobal uso = new UsoCuponGlobal();
        uso.setCuponId(cupon.getId());
        uso.setClienteId(request.clienteId());
        uso.setFechaUso(LocalDateTime.now());
        uso.setPedidoId(request.pedidoId());
        usoCuponGlobalRepository.save(uso);

        // Incrementar usos
        cupon.setUsosActuales(cupon.getUsosActuales() + 1);
        if (cupon.getUsoMaximoTotal() > 0 && cupon.getUsosActuales() >= cupon.getUsoMaximoTotal()) {
            cupon.setActivo(false);
        }
        cuponGlobalRepository.save(cupon);
    }

    // Verifica si el cliente ya usó un cupón de tipo PRIMERA_COMPRA
    public boolean clienteYaUsoTipo(UUID clienteId, TipoCupon tipo) {
        return cuponGlobalRepository.findByActivoTrue().stream()
                .filter(c -> c.getTipo() == tipo)
                .anyMatch(c -> usoCuponGlobalRepository.existsByCuponIdAndClienteId(c.getId(), clienteId));
    }

    private boolean estaExpirado(CuponGlobal c) {
        return c.getFechaExpiracion() != null && c.getFechaExpiracion().isBefore(LocalDate.now());
    }

    private boolean estaAgotado(CuponGlobal c) {
        return c.getUsoMaximoTotal() > 0 && c.getUsosActuales() >= c.getUsoMaximoTotal();
    }

    private CuponGlobalDto toDto(CuponGlobal c, UUID clienteId) {
        boolean yaUsado = usoCuponGlobalRepository.existsByCuponIdAndClienteId(c.getId(), clienteId);

        boolean aplicable = !yaUsado;

        // Para PRIMERA_COMPRA verificar que el cliente no tenga pedidos previos
        if (aplicable && c.getTipo() == TipoCupon.PRIMERA_COMPRA) {
            try {
                long pedidosPrevios = clientePedidoClient.contarPedidosUsuario(clienteId);
                aplicable = pedidosPrevios == 0;
            } catch (Exception e) {
                // Si el servicio Cliente no responde, se niega el cupón por seguridad
                aplicable = false;
            }
        }

        return new CuponGlobalDto(
                c.getId(),
                c.getNombre(),
                c.getDescripcion(),
                c.getTipo(),
                c.getDescuentoPorcentaje(),
                c.getDescuentoEnvio(),
                c.getFechaExpiracion(),
                aplicable
        );
    }
}
