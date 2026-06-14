package com.intifix.modules.auth.security;

import com.intifix.modules.auth.config.AuthProperties;
import com.intifix.modules.auth.entity.RolUsuario;
import com.intifix.modules.auth.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Emisión y validación de JWT (JJWT 0.12.x).
 *
 * <p>Contrato del token: {@code sub} = id del usuario (UUID), claims
 * adicionales {@code correo} y {@code roles}, y {@code typ} para distinguir
 * access de refresh (un refresh token jamás autentica una request HTTP).</p>
 */
@Component
@Slf4j
public class JwtTokenProvider {

    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    private static final String CLAIM_CORREO = "correo";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TOKEN_TYPE = "typ";
    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey signingKey;

    @Getter
    private final long accessTokenExpiration;

    @Getter
    private final long refreshTokenExpiration;

    public JwtTokenProvider(AuthProperties properties) {
        String secret = properties.getJwt().getSecret();
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
            // Fail-fast: arrancar firmando con un secret débil o por defecto
            // es una vulnerabilidad, no una configuración válida.
            throw new IllegalStateException(
                "app.jwt.secret debe tener al menos 256 bits (32 bytes). Configure la variable JWT_SECRET.");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = properties.getJwt().getAccessTokenExpiration();
        this.refreshTokenExpiration = properties.getJwt().getRefreshTokenExpiration();
    }

    public String generarAccessToken(UUID userId, String correo, Collection<RolUsuario> roles) {
        Instant now = Instant.now();
        List<String> roleNames = roles.stream().map(Enum::name).toList();

        return Jwts.builder()
            .subject(userId.toString())
            .claim(CLAIM_CORREO, correo)
            .claim(CLAIM_ROLES, roleNames)
            .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(accessTokenExpiration, ChronoUnit.SECONDS)))
            .signWith(signingKey, Jwts.SIG.HS256)
            .compact();
    }

    public String generarRefreshToken(UUID userId) {
        Instant now = Instant.now();

        return Jwts.builder()
            .subject(userId.toString())
            .id(UUID.randomUUID().toString())
            .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(refreshTokenExpiration, ChronoUnit.SECONDS)))
            .signWith(signingKey, Jwts.SIG.HS256)
            .compact();
    }

    /**
     * Valida firma, estructura y expiración. Lanza {@link InvalidTokenException}
     * específica; nunca retorna silenciosamente sobre un token inválido.
     */
    public void validarToken(String token) {
        try {
            Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw InvalidTokenException.expired();
        } catch (UnsupportedJwtException e) {
            throw InvalidTokenException.unsupported();
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw InvalidTokenException.malformed();
        } catch (SignatureException e) {
            throw InvalidTokenException.signature();
        } catch (Exception e) {
            log.error("Error inesperado al validar JWT", e);
            throw new InvalidTokenException("Error al validar token", e);
        }
    }

    public Claims extraerClaims(String token) {
        try {
            return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            throw InvalidTokenException.expired();
        } catch (Exception e) {
            throw new InvalidTokenException("Error al extraer claims del token", e);
        }
    }

    public UUID obtenerUserId(String token) {
        return obtenerUserId(extraerClaims(token));
    }

    public UUID obtenerUserId(Claims claims) {
        try {
            return UUID.fromString(claims.getSubject());
        } catch (Exception e) {
            throw new InvalidTokenException("Subject inválido en el token: se esperaba el UUID del usuario", e);
        }
    }

    public String obtenerCorreo(Claims claims) {
        return claims.get(CLAIM_CORREO, String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> obtenerRoles(Claims claims) {
        List<String> roles = claims.get(CLAIM_ROLES, List.class);
        return roles != null ? roles : List.of();
    }

    public String obtenerTipo(Claims claims) {
        return claims.get(CLAIM_TOKEN_TYPE, String.class);
    }

    /** Identificador único del refresh token (claim estándar jti). */
    public String obtenerTokenId(String token) {
        return extraerClaims(token).getId();
    }
}
