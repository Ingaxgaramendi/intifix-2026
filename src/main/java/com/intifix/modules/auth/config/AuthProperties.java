package com.intifix.modules.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binding tipado de la configuración de application.yml:
 *
 * <pre>
 * app:
 *   jwt:
 *     secret: ${JWT_SECRET}
 *     access-token-expiration: 900
 *     refresh-token-expiration: 604800
 *   auth:
 *     max-failed-attempts: 5
 * </pre>
 *
 * Antes el provider leía {@code ${jwt.secret:default}} — una clave que no
 * existe en el YAML — y firmaba con el secret hardcodeado por defecto.
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AuthProperties {

    private Jwt jwt = new Jwt();
    private Auth auth = new Auth();

    @Data
    public static class Jwt {
        private String secret;
        private long accessTokenExpiration = 900;
        private long refreshTokenExpiration = 604800;
    }

    @Data
    public static class Auth {
        private int maxFailedAttempts = 5;
    }
}
