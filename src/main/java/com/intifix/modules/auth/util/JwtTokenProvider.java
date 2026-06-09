package com.intifix.modules.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
        @Value("${app.jwt.secret:TuClaveSecretaUltraMegaSeguraQueNadiePuedeAdivinar1234567890!}") String secret,
        @Value("${app.jwt.access-expiration-ms:900000}") long accessTokenExpiration, // 15 Minutos
        @Value("${app.jwt.refresh-expiration-ms:604800000}") long refreshTokenExpiration) { // 7 Días
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generarAccessToken(String correo, Collection < ? extends GrantedAuthority > authorities) {
        List < String > roles = authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

        Claims claims = Jwts.claims().subject(correo).add("roles", roles).build();
        return construirToken(claims, accessTokenExpiration);
    }

    public String generarRefreshToken(String correo) {
        Claims claims = Jwts.claims().subject(correo).build(); // El refresh token va limpio, sin roles
        return construirToken(claims, refreshTokenExpiration);
    }

    private String construirToken(Claims claims, long expirationMs) {
        Date now = new Date();
        return Jwts.builder()
        .claims(claims)
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expirationMs))
        .signWith(secretKey)
        .compact();
    }

    public String obtenerCorreo(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    @SuppressWarnings("unchecked")
    public Set < String > obtenerRoles(String token) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        return Set.copyOf(claims.get("roles", List.class));
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
