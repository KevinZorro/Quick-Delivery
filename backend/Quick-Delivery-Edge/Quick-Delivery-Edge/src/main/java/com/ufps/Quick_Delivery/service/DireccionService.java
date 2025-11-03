package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.DireccionRequestDto;
import com.ufps.Quick_Delivery.dto.DireccionResponseDto;
import com.ufps.Quick_Delivery.model.Direccion;
import com.ufps.Quick_Delivery.model.TipoReferencia;
import com.ufps.Quick_Delivery.repository.DireccionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DireccionService {

    private final DireccionRepository direccionRepository;

    @Transactional
    public DireccionResponseDto crearDireccion(DireccionRequestDto requestDto) {
        Direccion direccion = Direccion.builder()
                .calle(requestDto.getCalle())
                .referencia(requestDto.getReferencia())
                .ciudad(requestDto.getCiudad())
                .barrio(requestDto.getBarrio())
                .coordenadas(requestDto.getCoordenadas())
                .usuario(requestDto.getUsuario())
                .tipoReferencia(requestDto.getTipoReferencia())
                .build();

        Direccion savedDireccion = direccionRepository.save(direccion);
        return mapToResponseDto(savedDireccion);
    }

    @Transactional(readOnly = true)
    public DireccionResponseDto obtenerDireccionPorId(UUID id) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con id: " + id));
        return mapToResponseDto(direccion);
    }

    @Transactional(readOnly = true)
    public List<DireccionResponseDto> obtenerTodasLasDirecciones() {
        return direccionRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DireccionResponseDto> obtenerDireccionesPorUsuario(UUID usuarioId) {
        return direccionRepository.findByUsuario(usuarioId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DireccionResponseDto> obtenerDireccionesPorCiudad(String ciudad) {
        return direccionRepository.findByCiudad(ciudad).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DireccionResponseDto> obtenerDireccionesPorUsuarioYTipo(UUID usuarioId, TipoReferencia tipo) {
        return direccionRepository.findByUsuarioAndTipoReferencia(usuarioId, tipo).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = false)
    public DireccionResponseDto actualizarDireccion(UUID id, DireccionRequestDto requestDto) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con id: " + id));

        direccion.setCalle(requestDto.getCalle());
        direccion.setReferencia(requestDto.getReferencia());
        direccion.setCiudad(requestDto.getCiudad());
        direccion.setBarrio(requestDto.getBarrio());
        direccion.setCoordenadas(requestDto.getCoordenadas());
        direccion.setUsuario(requestDto.getUsuario());
        direccion.setTipoReferencia(requestDto.getTipoReferencia());

        Direccion updatedDireccion = direccionRepository.save(direccion);
        return mapToResponseDto(updatedDireccion);
    }

    @Transactional
    public void eliminarDireccion(UUID id) {
        if (!direccionRepository.existsById(id)) {
            throw new RuntimeException("Dirección no encontrada con id: " + id);
        }
        direccionRepository.deleteById(id);
    }

    private DireccionResponseDto mapToResponseDto(Direccion direccion) {
        return DireccionResponseDto.builder()
                .id(direccion.getId())
                .calle(direccion.getCalle())
                .referencia(direccion.getReferencia())
                .ciudad(direccion.getCiudad())
                .barrio(direccion.getBarrio())
                .coordenadas(direccion.getCoordenadas())
                .usuario(direccion.getUsuario())
                .tipoReferencia(direccion.getTipoReferencia())
                .build();
    }
}
