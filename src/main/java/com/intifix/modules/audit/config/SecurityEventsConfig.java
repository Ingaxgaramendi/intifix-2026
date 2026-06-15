package com.intifix.modules.audit.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;

/**
 * Habilita la publicación de eventos de autorización de Spring Security
 * (en particular {@code AuthorizationDeniedEvent}) para que el módulo audit
 * pueda registrar los accesos denegados en {@code security_events} sin acoplar
 * al módulo auth.
 */
@Configuration
public class SecurityEventsConfig {

    @Bean
    public AuthorizationEventPublisher authorizationEventPublisher(ApplicationEventPublisher publisher) {
        return new SpringAuthorizationEventPublisher(publisher);
    }
}
