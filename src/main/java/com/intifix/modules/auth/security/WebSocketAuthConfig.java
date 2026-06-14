package com.intifix.modules.auth.security;

import com.intifix.shared.security.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;
import java.util.Set;

/**
 * Autenticación JWT del CONNECT STOMP. Vive en modules/auth (es autenticación);
 * la configuración genérica del broker vive en shared.config.WebSocketConfig.
 * Spring fusiona ambos WebSocketMessageBrokerConfigurer.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null || accessor.getCommand() != StompCommand.CONNECT) {
                    return message;
                }

                String token = extraerToken(accessor);
                if (token == null || token.isBlank()) {
                    return message;
                }

                try {
                    Claims claims = jwtTokenProvider.extraerClaims(token);
                    if (!JwtTokenProvider.TOKEN_TYPE_ACCESS.equals(jwtTokenProvider.obtenerTipo(claims))) {
                        return message;
                    }

                    List<String> roles = jwtTokenProvider.obtenerRoles(claims);
                    AuthenticatedUser principal = AuthenticatedUser.builder()
                        .id(jwtTokenProvider.obtenerUserId(claims))
                        .correo(jwtTokenProvider.obtenerCorreo(claims))
                        .roles(Set.copyOf(roles))
                        .build();

                    var authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList();

                    accessor.setUser(new UsernamePasswordAuthenticationToken(principal, null, authorities));
                } catch (Exception e) {
                    log.debug("CONNECT STOMP con token inválido: {}", e.getMessage());
                }
                return message;
            }
        });
    }

    private static String extraerToken(StompHeaderAccessor accessor) {
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String bearer = authHeaders.get(0);
            if (bearer != null && bearer.startsWith("Bearer ")) {
                return bearer.substring(7);
            }
        }
        List<String> tokenHeaders = accessor.getNativeHeader("token");
        return (tokenHeaders != null && !tokenHeaders.isEmpty()) ? tokenHeaders.get(0) : null;
    }
}
