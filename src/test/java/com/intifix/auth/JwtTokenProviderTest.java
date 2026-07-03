package com.intifix.auth;

import com.intifix.modules.auth.config.AuthProperties;
import com.intifix.modules.auth.entity.RolUsuario;
import com.intifix.modules.auth.exception.InvalidTokenException;
import com.intifix.modules.auth.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-key-at-least-32-bytes!!";

    private JwtTokenProvider provider;
    private UUID userId;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(buildProps(SECRET, 900L, 604800L));
        userId = UUID.randomUUID();
    }

    @Test
    void accessTokenContieneUserIdCorreoYRoles() {
        String token = provider.generarAccessToken(userId, "test@intifix.com", Set.of(RolUsuario.CLIENTE));

        Claims claims = provider.extraerClaims(token);

        assertThat(provider.obtenerUserId(claims)).isEqualTo(userId);
        assertThat(provider.obtenerCorreo(claims)).isEqualTo("test@intifix.com");
        assertThat(provider.obtenerRoles(claims)).containsExactly("CLIENTE");
        assertThat(provider.obtenerTipo(claims)).isEqualTo(JwtTokenProvider.TOKEN_TYPE_ACCESS);
    }

    @Test
    void refreshTokenTipoEsRefreshYTieneJti() {
        String token = provider.generarRefreshToken(userId);

        Claims claims = provider.extraerClaims(token);
        assertThat(provider.obtenerTipo(claims)).isEqualTo(JwtTokenProvider.TOKEN_TYPE_REFRESH);
        assertThat(claims.getId()).isNotBlank();
    }

    @Test
    void validarTokenPasaConTokenValido() {
        String token = provider.generarAccessToken(userId, "a@b.com", Set.of(RolUsuario.TECNICO));
        assertThatNoException().isThrownBy(() -> provider.validarToken(token));
    }

    @Test
    void validarTokenFallaConTokenMalformado() {
        assertThatThrownBy(() -> provider.validarToken("no.es.un.jwt"))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void validarTokenFallaConFirmaIncorrecta() {
        JwtTokenProvider otroProvider = new JwtTokenProvider(
            buildProps("otro-secret-key-at-least-32-bytes-!!", 900L, 604800L));
        String tokenAjeno = otroProvider.generarAccessToken(userId, "x@x.com", Set.of(RolUsuario.CLIENTE));

        assertThatThrownBy(() -> provider.validarToken(tokenAjeno))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void validarTokenFallaConTokenVencido() {
        JwtTokenProvider providerVencido = new JwtTokenProvider(buildProps(SECRET, 0L, 0L));
        String token = providerVencido.generarAccessToken(userId, "x@x.com", Set.of(RolUsuario.CLIENTE));

        assertThatThrownBy(() -> providerVencido.validarToken(token))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void constructorLanzaSiSecretEsMenorA32Bytes() {
        assertThatThrownBy(() -> new JwtTokenProvider(buildProps("corto", 900L, 604800L)))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("256 bits");
    }

    private static AuthProperties buildProps(String secret, long accessTtl, long refreshTtl) {
        AuthProperties.Jwt jwt = new AuthProperties.Jwt();
        jwt.setSecret(secret);
        jwt.setAccessTokenExpiration(accessTtl);
        jwt.setRefreshTokenExpiration(refreshTtl);

        AuthProperties props = new AuthProperties();
        props.setJwt(jwt);
        return props;
    }
}
