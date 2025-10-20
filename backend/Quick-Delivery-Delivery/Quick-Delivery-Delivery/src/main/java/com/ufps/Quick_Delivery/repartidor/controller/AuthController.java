package com.ufps.Quick_Delivery.repartidor.controller;

import com.ufps.Quick_Delivery.repartidor.dto.LoginRequest;
import com.ufps.Quick_Delivery.repartidor.dto.JwtResponse;
import com.ufps.Quick_Delivery.repartidor.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la autenticación de Repartidores.
 * Maneja las solicitudes de login y generación/cierre de sesión de JWT.
 * @author Ranita_Dardo_Dorada
 */

@RestController
@RequestMapping("/api/auth/repartidor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite la comunicación desde el frontend
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    // LOGIN
    /**
     * Endpoint para autenticar a un repartidor y emitir un token JWT.
     * @param loginRequest Objeto que contiene el email y la contraseña del repartidor.
     * @return ResponseEntity con el token JWT (JwtResponse) si la autenticación es exitosa.
     * @throws AuthenticationException Si el email o la contraseña son inválidos.
     */

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Genera el token y lo devuelve
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                "Bearer",
                userDetails.getUsername()
        ));
    }

    // LOGOUT (Cliente debe eliminar el token)
    /**
     * Endpoint para el "cierre de sesión".
     * En una arquitectura JWT sin estado, simplemente limpia el contexto de seguridad del servidor
     * (aunque el token sigue siendo válido hasta que expira). El cliente debe destruir el token.
     * @return Mensaje de confirmación de cierre de sesión.
     */

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext(); 
        return ResponseEntity.ok("Sesión de repartidor cerrada exitosamente.");
    }
}