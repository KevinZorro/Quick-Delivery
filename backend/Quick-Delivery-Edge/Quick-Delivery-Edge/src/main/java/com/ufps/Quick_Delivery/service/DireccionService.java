package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.DireccionRequestDto;
import com.ufps.Quick_Delivery.dto.DireccionResponseDto;
import com.ufps.Quick_Delivery.dto.GeocodingResponseDto;
import com.ufps.Quick_Delivery.model.Direccion;
import com.ufps.Quick_Delivery.model.Rol;
import com.ufps.Quick_Delivery.model.Usuario;
import com.ufps.Quick_Delivery.repository.DireccionRepository;
import com.ufps.Quick_Delivery.repository.UsuarioRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DireccionService {
    
    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final GoogleMapsService googleMapsService; // ⭐ NUEVO
    
    @Transactional
    public DireccionResponseDto crearDireccion(DireccionRequestDto requestDto,@NonNull UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // ⭐ VALIDACIÓN: Si es DELIVERY, solo puede tener 1 dirección
        if (usuario.getRol() == Rol.REPARTIDOR) {
            long cantidadDirecciones = direccionRepository.countByUsuarioId(usuarioId);
            
            if (cantidadDirecciones >= 1) {
                throw new RuntimeException("Los repartidores solo pueden tener una dirección registrada");
            }
        }
        
        // ⭐ NUEVO: Auto-geocodificar si no vienen coordenadas
        String coordenadas = requestDto.getCoordenadas();
        
        if (coordenadas == null || coordenadas.trim().isEmpty()) {
            try {
                log.info("Geocodificando dirección automáticamente para usuario: {}", usuarioId);
                
                // Construir dirección completa para geocodificación
                String fullAddress = String.format("%s, %s, %s, Colombia", 
                    requestDto.getCalle(), 
                    requestDto.getBarrio(), 
                    requestDto.getCiudad()
                );
                
                // Llamar al servicio de Google Maps
                GeocodingResponseDto geocoded = googleMapsService.geocodeAddress(fullAddress);
                
                // Guardar en formato "latitud,longitud"
                coordenadas = geocoded.getLatitude() + "," + geocoded.getLongitude();
                
                log.info("Geocodificación exitosa: {} -> {}", fullAddress, coordenadas);
                
            } catch (Exception e) {
                log.warn("No se pudo geocodificar la dirección: {}. Continuando sin coordenadas.", e.getMessage());
                // Si falla la geocodificación, continuar sin coordenadas
                coordenadas = null;
            }
        }
        
        Direccion direccion = new Direccion();
        direccion.setUsuario(usuario);
        direccion.setCalle(requestDto.getCalle());
        direccion.setReferencia(requestDto.getReferencia());
        direccion.setCiudad(requestDto.getCiudad());
        direccion.setBarrio(requestDto.getBarrio());
        direccion.setCoordenadas(coordenadas); // ⭐ Coordenadas auto-geocodificadas
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
    public DireccionResponseDto obtenerDireccionPorId(@NonNull UUID id) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        return convertirAResponseDto(direccion);
    }
    
    @Transactional
    public DireccionResponseDto actualizarDireccion(@NonNull UUID id, DireccionRequestDto requestDto,@NonNull UUID usuarioId) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        
        // ⭐ Verificar que la dirección pertenezca al usuario
        if (!direccion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para actualizar esta dirección");
        }
        
        // ⭐ NUEVO: Auto-geocodificar si cambió la dirección y no vienen coordenadas
        String coordenadas = requestDto.getCoordenadas();
        
        // Verificar si la dirección cambió
        boolean direccionCambio = !direccion.getCalle().equals(requestDto.getCalle()) ||
                                  !direccion.getCiudad().equals(requestDto.getCiudad()) ||
                                  !direccion.getBarrio().equals(requestDto.getBarrio());
        
        if (direccionCambio && (coordenadas == null || coordenadas.trim().isEmpty())) {
            try {
                log.info("Geocodificando dirección actualizada para direccion ID: {}", id);
                
                String fullAddress = String.format("%s, %s, %s, Colombia", 
                    requestDto.getCalle(), 
                    requestDto.getBarrio(), 
                    requestDto.getCiudad()
                );
                
                GeocodingResponseDto geocoded = googleMapsService.geocodeAddress(fullAddress);
                coordenadas = geocoded.getLatitude() + "," + geocoded.getLongitude();
                
                log.info("Geocodificación exitosa en actualización: {}", coordenadas);
                
            } catch (Exception e) {
                log.warn("No se pudo geocodificar la dirección actualizada: {}", e.getMessage());
                // Mantener las coordenadas anteriores si falla
                coordenadas = direccion.getCoordenadas();
            }
        } else if (coordenadas == null || coordenadas.trim().isEmpty()) {
            // Si no hay coordenadas nuevas y no cambió la dirección, mantener las antiguas
            coordenadas = direccion.getCoordenadas();
        }
        
        direccion.setCalle(requestDto.getCalle());
        direccion.setReferencia(requestDto.getReferencia());
        direccion.setCiudad(requestDto.getCiudad());
        direccion.setBarrio(requestDto.getBarrio());
        direccion.setCoordenadas(coordenadas);
        direccion.setTipoReferencia(requestDto.getTipoReferencia());
        
        Direccion direccionActualizada = direccionRepository.save(direccion);
        return convertirAResponseDto(direccionActualizada);
    }
    
    @Transactional
    public void eliminarDireccion(@NonNull UUID id, UUID usuarioId) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        
        // ⭐ Verificar que la dirección pertenezca al usuario
        if (!direccion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta dirección");
        }
        
        direccionRepository.deleteById(id);
    }
    
    // ⭐ NUEVO: Método para re-geocodificar una dirección manualmente
    @Transactional
    public DireccionResponseDto regeocodeAddress(@NonNull UUID direccionId, UUID usuarioId) {
        Direccion direccion = direccionRepository.findById(direccionId)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        
        if (!direccion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para modificar esta dirección");
        }
        
        try {
            String fullAddress = String.format("%s, %s, %s, Colombia", 
                direccion.getCalle(), 
                direccion.getBarrio(), 
                direccion.getCiudad()
            );
            
            GeocodingResponseDto geocoded = googleMapsService.geocodeAddress(fullAddress);
            String coordenadas = geocoded.getLatitude() + "," + geocoded.getLongitude();
            
            direccion.setCoordenadas(coordenadas);
            Direccion direccionActualizada = direccionRepository.save(direccion);
            
            log.info("Dirección re-geocodificada exitosamente: ID={}, Coords={}", direccionId, coordenadas);
            
            return convertirAResponseDto(direccionActualizada);
            
        } catch (Exception e) {
            log.error("Error al re-geocodificar dirección ID {}: {}", direccionId, e.getMessage());
            throw new RuntimeException("Error al geocodificar la dirección: " + e.getMessage());
        }
    }
    
    // ⭐ NUEVO: Obtener direcciones con coordenadas válidas solamente
    @Transactional(readOnly = true)
    public List<DireccionResponseDto> obtenerDireccionesConCoordenadas(UUID usuarioId) {
        return direccionRepository.findByUsuarioId(usuarioId).stream()
                .filter(d -> d.getCoordenadas() != null && !d.getCoordenadas().trim().isEmpty())
                .map(this::convertirAResponseDto)
                .collect(Collectors.toList());
    }
    
    private DireccionResponseDto convertirAResponseDto(Direccion direccion) {
        DireccionResponseDto dto = new DireccionResponseDto();
        dto.setId(direccion.getId());
        dto.setUsuarioId(direccion.getUsuario().getId());
        dto.setCalle(direccion.getCalle());
        dto.setReferencia(direccion.getReferencia());
        dto.setCiudad(direccion.getCiudad());
        dto.setBarrio(direccion.getBarrio());
        dto.setCoordenadas(direccion.getCoordenadas());
        dto.setTipoReferencia(direccion.getTipoReferencia());
        return dto;
    }
}
