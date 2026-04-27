package edu.upn.clinica.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// ============================================================
//  JwtUtil.java
//  Genera y valida tokens JWT
//  Usado por: JwtFilter, AuthService
// ============================================================
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration; // milisegundos (86400000 = 24h)

    // --- Generar token ---
    public String generateToken(String email, String rol) {
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    // --- Extraer email del token ---
    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    // --- Extraer rol del token ---
    public String getRol(String token) {
        return getClaims(token).get("rol", String.class);
    }

    // --- Validar token ---
    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // --- Claims internos ---
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // --- Clave secreta ---
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}