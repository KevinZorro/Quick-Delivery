package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.dto.DeliveryUserDto;
import com.ufps.Quick_Delivery.models.DeliveryUser;
import com.ufps.Quick_Delivery.repository.DeliveryUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryUserService {

    private final DeliveryUserRepository repository;

    // Convertir entidad a DTO
    private DeliveryUserDto toDto(DeliveryUser entity) {
        DeliveryUserDto dto = new DeliveryUserDto();
        dto.setId(entity.getId());
        dto.setUsuarioId(entity.getUsuarioId());
        dto.setVehiculo(entity.getVehiculo());
        dto.setCalificacionPromedio(entity.getCalificacionPromedio());
        return dto;
    }

    // Convertir DTO a entidad
    private DeliveryUser toEntity(DeliveryUserDto dto) {
        return DeliveryUser.builder()
                .id(dto.getId())
                .usuarioId(dto.getUsuarioId())
                .vehiculo(dto.getVehiculo())
                .calificacionPromedio(dto.getCalificacionPromedio())
                .build();
    }

    public List<DeliveryUserDto> findAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<DeliveryUserDto> findById(UUID id) {
        return repository.findById(id).map(this::toDto);
    }

    public DeliveryUserDto save(DeliveryUserDto dto) {
        DeliveryUser entity = toEntity(dto);
        DeliveryUser saved = repository.save(entity);
        return toDto(saved);
    }

    public Optional<DeliveryUserDto> update(UUID id, DeliveryUserDto dto) {
        return repository.findById(id).map(existing -> {
            existing.setUsuarioId(dto.getUsuarioId());
            existing.setVehiculo(dto.getVehiculo());
            existing.setCalificacionPromedio(dto.getCalificacionPromedio());
            DeliveryUser updated = repository.save(existing);
            return toDto(updated);
        });
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}