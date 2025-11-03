package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.DireccionRequestDto;
import com.ufps.Quick_Delivery.dto.DireccionResponseDto;
import com.ufps.Quick_Delivery.model.Direccion;
import com.ufps.Quick_Delivery.model.Rol;
import com.ufps.Quick_Delivery.model.Usuario;
import com.ufps.Quick_Delivery.repository.DireccionRepository;
import com.ufps.Quick_Delivery.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;
    
    @Transactional
    public DireccionResponseDto crearDireccion(DireccionRequestDto requestDto, UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // ⭐ VALIDACIÓN: Si es DELIVERY, solo puede tener 1 dirección
        if (usuario.getRol() == Rol.REPARTIDOR) {
            long cantidadDirecciones = direccionRepository.countByUsuarioId(usuarioId);
            
            if (cantidadDirecciones >= 1) {
                throw new RuntimeException("Los repartidores solo pueden tener una dirección registrada");
            }
        }
        
        Direccion direccion = new Direccion();
        direccion.setUsuario(usuario);
        direccion.setCalle(requestDto.getCalle());
        direccion.setReferencia(requestDto.getReferencia());
        direccion.setCiudad(requestDto.getCiudad());
        direccion.setBarrio(requestDto.getBarrio());
        direccion.setCoordenadas(requestDto.getCoordenadas());
        direccion.setTipoReferencia(requestDto.getTipoReferencia());
        
        Direccion direccionGuardada = direccionRepository.save(direccion);
        return convertirAResponseDto(direccionGuardada);
    }
    
    @Transactional(readOnly = true)
    public List<DireccionResponseDto> obtenerDireccionesPorUsuario(UUID usuarioId) {
        return direccionRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirAResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public DireccionResponseDto obtenerDireccionPorId(UUID id) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        return convertirAResponseDto(direccion);
    }
    
    @Transactional
    public DireccionResponseDto actualizarDireccion(UUID id, DireccionRequestDto requestDto, UUID usuarioId) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        
        // ⭐ Verificar que la dirección pertenezca al usuario
        if (!direccion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para actualizar esta dirección");
        }
        
        direccion.setCalle(requestDto.getCalle());
        direccion.setReferencia(requestDto.getReferencia());
        direccion.setCiudad(requestDto.getCiudad());
        direccion.setBarrio(requestDto.getBarrio());
        direccion.setCoordenadas(requestDto.getCoordenadas());
        direccion.setTipoReferencia(requestDto.getTipoReferencia());
        
        Direccion direccionActualizada = direccionRepository.save(direccion);
        return convertirAResponseDto(direccionActualizada);
    }
    
    @Transactional
    public void eliminarDireccion(UUID id, UUID usuarioId) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        
        // ⭐ Verificar que la dirección pertenezca al usuario
        if (!direccion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta dirección");
        }
        
        direccionRepository.deleteById(id);
    }
    
    private DireccionResponseDto convertirAResponseDto(Direccion direccion) {
        DireccionResponseDto Dto = new DireccionResponseDto();
        Dto.setId(direccion.getId());
        Dto.setUsuarioId(direccion.getUsuario().getId());
        Dto.setCalle(direccion.getCalle());
        Dto.setReferencia(direccion.getReferencia());
        Dto.setCiudad(direccion.getCiudad());
        Dto.setBarrio(direccion.getBarrio());
        Dto.setCoordenadas(direccion.getCoordenadas());
        Dto.setTipoReferencia(direccion.getTipoReferencia());
        return Dto;
    }
}
