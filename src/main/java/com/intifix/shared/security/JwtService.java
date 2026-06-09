package com.intifix.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    private final SecurityProperties props;
    private final SecretKey key;

    public JwtService(SecurityProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(ensureBase64(props.hmacSecret())));
    }

    public String issueAccessToken(UUID userId, String role, String jti) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.accessTokenTtlSeconds());

        return Jwts.builder()
                .issuer(props.issuer())
                .subject(userId.toString())
                .id(jti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(Map.of(
                        "role", role,
                        "typ", TOKEN_TYPE_ACCESS
                ))
                .signWith(key)
                .compact();
    }

    public String issueRefreshToken(UUID userId, String role, String jti) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.refreshTokenTtlSeconds());

        return Jwts.builder()
                .issuer(props.issuer())
                .subject(userId.toString())
                .id(jti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(Map.of(
                        "role", role,
                        "typ", TOKEN_TYPE_REFRESH
                ))
                .signWith(key)
                .compact();
    }

    public JwtClaims parseAndValidate(String jwt) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(props.issuer())
                .build()
                .parseSignedClaims(jwt);

        Claims c = jws.getPayload();
        UUID userId = UUID.fromString(c.getSubject());
        String role = c.get("role", String.class);
        String typ = c.get("typ", String.class);
        String jti = c.getId();
        return new JwtClaims(userId, role, typ, jti);
    }

    private static String ensureBase64(String maybePlain) {
        // Expecting BASE64. If plain text is provided, this avoids runtime crashes by encoding it.
        // For production, set INTIFIX_JWT_HMAC_SECRET as a base64-encoded 256-bit+ key.
        try {
            Decoders.BASE64.decode(maybePlain);
            return maybePlain;
        } catch (Exception ignored) {
            return java.util.Base64.getEncoder().encodeToString(maybePlain.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }
}

