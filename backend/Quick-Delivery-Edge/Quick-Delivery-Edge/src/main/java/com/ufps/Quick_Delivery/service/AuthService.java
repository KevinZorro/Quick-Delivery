package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.ClienteClient;
import com.ufps.Quick_Delivery.dto.LoginRequestDto;
import com.ufps.Quick_Delivery.dto.LoginResponseDto;
import com.ufps.Quick_Delivery.dto.UsuarioDto;
import com.ufps.Quick_Delivery.model.Usuario;
import com.ufps.Quick_Delivery.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ufps.Quick_Delivery.security.JwtService;
import com.ufps.Quick_Delivery.client.RestauranteClient;
import com.ufps.Quick_Delivery.client.DeliveryClient;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteClient clienteClient;
    private final RestauranteClient restauranteClient;
    private final DeliveryClient deliveryClient;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public Usuario registrar(UsuarioDto dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());
        usuario.setTelefono(dto.getTelefono());
        usuario.setContraseña(passwordEncoder.encode(dto.getContraseña()));
        usuario.setRol(dto.getRol());
        usuario.setActivo(true);
        usuario.setFecharegistro(LocalDateTime.now());

        Usuario guardado = usuarioRepository.save(usuario);

        // Llamada a microservicio según rol
        switch (guardado.getRol()) {
            case CLIENTE:
                ClienteClient.ClienteRequest clienteRequest = new ClienteClient.ClienteRequest();
                clienteRequest.setUsuarioId(guardado.getId());
                clienteClient.crearCliente(clienteRequest);
                break;
            case RESTAURANTE:
                RestauranteClient.RestauranteRequest restauranteRequest = new RestauranteClient.RestauranteRequest();
                restauranteRequest.setUsuarioId(guardado.getId());
                restauranteRequest.setDescripcion((String) dto.getDetalles().get("descripcion"));
                restauranteRequest.setCategoria((String) dto.getDetalles().get("categoria"));
                restauranteClient.crearRestaurante(restauranteRequest);
                break;
            case REPARTIDOR:
                DeliveryClient.DeliveryRequest deliveryRequest = new DeliveryClient.DeliveryRequest();
                deliveryRequest.setUsuarioId(guardado.getId());
                deliveryRequest.setVehiculo((String) dto.getDetalles().get("vehiculo"));
                deliveryClient.crearDelivery(deliveryRequest);
                break;
            default:
                // No hace nada
        }

        return guardado;
    }

    // ⭐ Login actualizado para incluir userId en la respuesta
    public Optional<LoginResponseDto> login(LoginRequestDto dto) {
        try {
            return usuarioRepository.findByCorreo(dto.getCorreo())
                    .filter(usuario -> passwordEncoder.matches(dto.getContraseña(), usuario.getContraseña()))
                    .map(usuario -> {
                        String token = jwtService.generateToken(usuario);
                        LoginResponseDto res = new LoginResponseDto();
                        res.setToken(token);
                        res.setUserId(usuario.getId());  // ⭐ AGREGAR UUID
                        res.setNombre(usuario.getNombre());
                        res.setCorreo(usuario.getCorreo());
                        res.setRol(usuario.getRol().name());
                        return res;
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
