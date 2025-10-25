package com.ufps.Quick_Delivery.services;


import com.ufps.Quick_Delivery.dto.AuthResponse;
import com.ufps.Quick_Delivery.dto.LoginRequest;
import com.ufps.Quick_Delivery.dto.RegisterRequest;
import com.ufps.Quick_Delivery.dto.MessageResponse;
import com.ufps.Quick_Delivery.models.DeliveryUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private DeliveryUserService deliveryUserService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        if (deliveryUserService.existsByCorreo(request.getCorreo())) {
            return new AuthResponse(null, null, null, null, null, "Error: El correo ya está registrado");
        }

        DeliveryUser user = new DeliveryUser(
                request.getNombre(),
                request.getCorreo(),
                request.getContraseña(),
                request.getTelefono(),
                request.getVehiculo()
        );

        DeliveryUser savedUser = deliveryUserService.save(user);
        String token = jwtService.generateToken(savedUser.getCorreo());

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getNombre(),
                savedUser.getCorreo(),
                savedUser.getVehiculo(),
                "Repartidor registrado exitosamente"
        );
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Optional<DeliveryUser> userOpt = deliveryUserService.findByCorreoAndActivo(request.getCorreo());
            if (userOpt.isEmpty()) {
                return new AuthResponse(null, null, null, null, null, "Error: Usuario no encontrado o inactivo");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getContraseña())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            DeliveryUser user = userOpt.get();
            String token = jwtService.generateToken(user.getCorreo());

            return new AuthResponse(
                    token,
                    user.getId(),
                    user.getNombre(),
                    user.getCorreo(),
                    user.getVehiculo(),
                    "Login exitoso"
            );

        } catch (Exception e) {
            return new AuthResponse(null, null, null, null, null, "Error: Credenciales inválidas");
        }
    }

    public MessageResponse logout(String token) {
        try {
            jwtService.invalidateToken(token);
            return new MessageResponse("Sesión cerrada exitosamente");
        } catch (Exception e) {
            return new MessageResponse("Error al cerrar sesión");
        }
    }

    public MessageResponse softDeleteAccount(UUID userId, String currentPassword, String token) {
        try {
            Optional<DeliveryUser> userOpt = deliveryUserService.findById(userId);
            if (userOpt.isEmpty()) {
                return new MessageResponse("Usuario no encontrado");
            }

            DeliveryUser user = userOpt.get();

            if (!passwordEncoder.matches(currentPassword, user.getContraseña())) {
                return new MessageResponse("Contraseña incorrecta");
            }

            deliveryUserService.softDelete(userId);

            if (token != null) {
                jwtService.invalidateToken(token);
            }

            return new MessageResponse("Cuenta eliminada exitosamente");

        } catch (Exception e) {
            return new MessageResponse("Error al eliminar la cuenta");
        }
    }

    public UUID getUserIdByEmail(String email) {
        Optional<DeliveryUser> user = deliveryUserService.findByCorreo(email);
        return user.map(DeliveryUser::getId).orElse(null);
    }
}