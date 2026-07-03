package com.intifix.modules.auth.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private static final String PREFIX = "pwd_reset:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveToken(String token, UUID userId, long ttlSeconds) {
        redisTemplate.opsForValue().set(PREFIX + token, userId.toString(), ttlSeconds, TimeUnit.SECONDS);
        log.debug("Password reset token guardado para usuario: {}", userId);
    }

    @Override
    public Optional<UUID> findByToken(String token) {
        Object value = redisTemplate.opsForValue().get(PREFIX + token);
        if (value == null) return Optional.empty();
        try {
            return Optional.of(UUID.fromString(value.toString()));
        } catch (IllegalArgumentException e) {
            log.error("Valor corrupto en Redis para password reset token; se trata como inválido.");
            return Optional.empty();
        }
    }

    @Override
    public void deleteToken(String token) {
        redisTemplate.delete(PREFIX + token);
        log.debug("Password reset token eliminado.");
    }
}
