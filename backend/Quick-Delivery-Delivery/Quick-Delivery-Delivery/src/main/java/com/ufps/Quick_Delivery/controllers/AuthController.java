package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.dto.*;
import com.ufps.Quick_Delivery.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        if (response.getToken() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        if (response.getToken() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            MessageResponse response = authService.logout(token);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Token no proporcionado"));
    }

    @DeleteMapping("/account")
    public ResponseEntity<MessageResponse> deleteAccount(
            @Valid @RequestBody DeleteAccountRequest deleteRequest,
            HttpServletRequest request,
            Authentication authentication) {

        try {
            String token = extractTokenFromRequest(request);
            String userEmail = authentication.getName();
            UUID userId = authService.getUserIdByEmail(userEmail);

            if (userId == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Usuario no encontrado"));
            }

            MessageResponse response = authService.softDeleteAccount(userId, deleteRequest.getCurrentPassword(), token);

            if (response.getMessage().contains("exitosamente")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error al eliminar la cuenta"));
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}