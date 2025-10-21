package com.ufps.Quick_Delivery.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final String SECRET_KEY = "EsteEsUnSecretoMuyLargoParaFirmarElTokenJWT123456"; // cámbialo por algo más seguro
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // 👉 Generar token con correo, rol e ID
    public String generateToken(String correo, String role, Long id) {
        return Jwts.builder()
                .setSubject(correo)
                .claim("role", role)
                .claim("id", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 👉 Validar token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // 👉 Extraer correo del token
    public String extractCorreo(String token) {
        return getClaims(token).getSubject();
    }

    // 👉 Extraer rol del token
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
