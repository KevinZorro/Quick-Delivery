package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.dto.DeliveryUserDto;
import com.ufps.Quick_Delivery.models.DeliveryUser;
import com.ufps.Quick_Delivery.repository.DeliveryUserRepository;
import com.ufps.Quick_Delivery.client.ClienteDireccion;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryUserService {

    private final DeliveryUserRepository repository;
    private final ClienteDireccion clienteDireccion;

    // Convertir entidad a DTO
    private DeliveryUserDto toDto(DeliveryUser entity) {
        DeliveryUserDto dto = new DeliveryUserDto();
        dto.setId(entity.getId());
        dto.setUsuarioId(entity.getUsuarioId());
        dto.setVehiculo(entity.getVehiculo());
        dto.setCalificacionPromedio(entity.getCalificacionPromedio());
        dto.setGanancias(entity.getGanancias());
        dto.setLatitud(entity.getLatitud());
        dto.setLongitud(entity.getLongitud());
        dto.setRangoKm(entity.getRangoKm());
        return dto;
    }

    // Convertir DTO a entidad
    private DeliveryUser toEntity(DeliveryUserDto dto) {
        return DeliveryUser.builder()
                .id(dto.getId())
                .usuarioId(dto.getUsuarioId())
                .vehiculo(dto.getVehiculo())
                .calificacionPromedio(dto.getCalificacionPromedio())
                .ganancias(dto.getGanancias())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .rangoKm(dto.getRangoKm())
                .build();
    }

    public List<DeliveryUserDto> findAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<DeliveryUserDto> findById(@NonNull UUID id) {
        return repository.findById(id).map(this::toDto);
    }

    public Optional<DeliveryUserDto> findByUsuarioId(@NonNull UUID usuarioId) {
        return repository.findByUsuarioId(usuarioId).map(this::toDto);
    }

    public DeliveryUserDto save(DeliveryUserDto dto) {
        DeliveryUser entity = toEntity(dto);
        DeliveryUser saved = repository.save(entity);

        // ⭐ Crear dirección inicial en Edge para el repartidor
        // Esto asegura que la dirección exista cuando se intente actualizar la
        // ubicación
        try {
            // Llamar a actualizarUbicacion con coordenadas vacías para crear la dirección
            // El método en Edge creará automáticamente la dirección si no existe
            // Las coordenadas se actualizarán cuando el repartidor actualice su ubicación
            // real
            ResponseEntity<Void> response = clienteDireccion.actualizarUbicacion(
                    saved.getUsuarioId(),
                    "" // Coordenadas iniciales vacías, se actualizarán cuando el repartidor actualice
                       // su ubicación
            );

            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Dirección inicial creada para repartidor: " + saved.getUsuarioId());
            } else {
                System.err.println("⚠️ No se pudo crear la dirección inicial para repartidor " +
                        saved.getUsuarioId() + ": Respuesta no exitosa");
            }
        } catch (Exception e) {
            // Si falla la creación de la dirección, loguear pero no fallar la creación del
            // delivery
            System.err.println("⚠️ No se pudo crear la dirección inicial para repartidor " +
                    saved.getUsuarioId() + ": " + e.getMessage());
            // No lanzar excepción para no fallar la creación del delivery
        }

        return toDto(saved);
    }

    public Optional<DeliveryUserDto> update(@NonNull UUID id, DeliveryUserDto dto) {
        return repository.findById(id).map(existing -> {
            existing.setUsuarioId(dto.getUsuarioId());
            existing.setVehiculo(dto.getVehiculo());
            existing.setCalificacionPromedio(dto.getCalificacionPromedio());
            existing.setLatitud(dto.getLatitud());
            existing.setLongitud(dto.getLongitud());
            if (dto.getRangoKm() != null) {
                existing.setRangoKm(dto.getRangoKm());
            }
            DeliveryUser updated = repository.save(existing);
            return toDto(updated);
        });
    }

    public void delete(@NonNull UUID id) {
        repository.deleteById(id);
    }

    // NUEVO: registrar ganancia
    public void registrarGanancia(UUID deliveryUserId, double valorVenta) {
        DeliveryUser du = repository.findById(deliveryUserId)
                .orElseThrow(() -> new RuntimeException("DeliveryUser no encontrado"));

        double gananciaNueva = valorVenta * 0.05;

        // Verificar si ganancias es null
        Double gananciaActual = du.getGanancias();
        if (gananciaActual == null) {
            gananciaActual = 0.0;
        }

        du.setGanancias(gananciaActual + gananciaNueva);
        repository.save(du);
    }

    // ⭐ AGREGAR en DeliveryUserService
    public Optional<UUID> findDeliveryIdByUsuarioId(UUID usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .map(DeliveryUser::getId);
    }

    public boolean actualizarUbicacion(UUID usuarioId, double latitud, double longitud) {
        String coordenadas = latitud + "," + longitud;
        ResponseEntity<Void> response = clienteDireccion.actualizarUbicacion(usuarioId, coordenadas);
        return response.getStatusCode().is2xxSuccessful();
    }

}
