package com.intifix.modules.chat.service.impl;

import com.intifix.modules.chat.dto.response.PresenciaResponse;
import com.intifix.modules.chat.service.PresenciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenciaServiceImpl implements PresenciaService {

    private final StringRedisTemplate redisTemplate;

    // Online expira solo: si el heartbeat/sesión muere, el usuario cae a offline.
    private static final Duration TTL_ONLINE = Duration.ofSeconds(60);
    private static final String KEY_ONLINE = "chat:online:";
    private static final String KEY_ULTIMA_CONEXION = "chat:lastseen:";

    @Override
    public void marcarOnline(UUID idUsuario) {
        redisTemplate.opsForValue().set(KEY_ONLINE + idUsuario, "1", TTL_ONLINE);
        log.debug("Usuario online: {}", idUsuario);
    }

    @Override
    public void marcarOffline(UUID idUsuario) {
        redisTemplate.delete(KEY_ONLINE + idUsuario);
        redisTemplate.opsForValue().set(KEY_ULTIMA_CONEXION + idUsuario, Instant.now().toString());
        log.debug("Usuario offline: {}", idUsuario);
    }

    @Override
    public boolean estaOnline(UUID idUsuario) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_ONLINE + idUsuario));
    }

    @Override
    public PresenciaResponse obtenerPresencia(UUID idUsuario) {
        boolean online = estaOnline(idUsuario);
        String ultima = redisTemplate.opsForValue().get(KEY_ULTIMA_CONEXION + idUsuario);
        Instant ultimaConexion = (ultima != null) ? Instant.parse(ultima) : null;
        return PresenciaResponse.builder()
                .idUsuario(idUsuario)
                .online(online)
                .ultimaConexion(ultimaConexion)
                .build();
    }
}
