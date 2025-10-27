package com.ufps.Quick_Delivery.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
    private final long expirationMs = 86400000; // 24 horas

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    public String generateToken(String correo) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                return false;
            }

            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            tokenBlacklistService.addToBlacklist(token, e.getClaims().getExpiration());
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void invalidateToken(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            tokenBlacklistService.addToBlacklist(token, expiration);
        } catch (Exception e) {
            tokenBlacklistService.addToBlacklist(token, new Date(System.currentTimeMillis() + expirationMs));
        }
    }

    private Date getExpirationFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public Date getExpirationDate(String token) {
        return getExpirationFromToken(token);
    }
}