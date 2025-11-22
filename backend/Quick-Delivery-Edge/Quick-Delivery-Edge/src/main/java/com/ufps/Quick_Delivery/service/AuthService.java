package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.ClienteClient;
import com.ufps.Quick_Delivery.dto.LoginRequestDto;
import com.ufps.Quick_Delivery.dto.LoginResponseDto;
import com.ufps.Quick_Delivery.dto.UsuarioDto;
import com.ufps.Quick_Delivery.model.PasswordResetToken;
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
import com.ufps.Quick_Delivery.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Value;


import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteClient clienteClient;
    private final RestauranteClient restauranteClient;
    private final DeliveryClient deliveryClient;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

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
        System.out.println("${cliente-service.url}");
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

    public boolean verificarCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    // ⭐ Login actualizado para incluir userId en la respuesta y validar usuarios activos
    public Optional<LoginResponseDto> login(LoginRequestDto dto) {
        try {
            return usuarioRepository.findByCorreo(dto.getCorreo())
                    .filter(usuario -> usuario.isActivo()) // ⭐ Validar que el usuario esté activo
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


    @Transactional
public void enviarTokenRecuperacion(String correo) {
    Usuario usuario = usuarioRepository.findByCorreo(correo)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    passwordResetTokenRepository.deleteByUsuario(usuario); // solo uno activo

    String token = UUID.randomUUID().toString();
    LocalDateTime expiration = LocalDateTime.now().plusDays(30); // 30 min de vigencia

    PasswordResetToken resetToken = new PasswordResetToken();
    resetToken.setUsuario(usuario);
    resetToken.setToken(token);
    resetToken.setExpiration(expiration);
    System.out.println("Generando token: " + token + " para usuario: " + correo + " con expiración: " + expiration);
    passwordResetTokenRepository.save(resetToken);

    // ENVÍO DE CORREO
    String link = frontendUrl + "/reset-password?token=" + token;
    emailService.send(
        correo,
        "Recupera tu contraseña",
        "Haz clic aquí para restablecer tu contraseña: " + link
    );
}

public boolean validarToken(String token) {
    Optional<PasswordResetToken> opt = passwordResetTokenRepository.findByToken(token);
    if (opt.isEmpty()) {
        System.out.println("Token no encontrado: " + token);
        return false;
    }
    PasswordResetToken resetToken = opt.get();
    System.out.println("Token recibido: " + token + ", expiración: " + resetToken.getExpiration());
    if (resetToken.getExpiration().isBefore(LocalDateTime.now())) {
        System.out.println("Token expirado");
        return false;
    }
    System.out.println("Token válido");
    return true;
}


@Transactional
public boolean actualizarContrasena(String token, String nuevaContrasena) {
    Optional<PasswordResetToken> opt = passwordResetTokenRepository.findByToken(token);
    if (opt.isEmpty()) return false;
    PasswordResetToken resetToken = opt.get();
    if (resetToken.getExpiration().isBefore(LocalDateTime.now())) return false;

    Usuario usuario = resetToken.getUsuario();
    usuario.setContraseña(passwordEncoder.encode(nuevaContrasena));
    usuarioRepository.save(usuario);
    passwordResetTokenRepository.delete(resetToken);
    return true;
}

}
