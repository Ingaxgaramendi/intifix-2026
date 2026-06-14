package com.intifix.modules.auth.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Implementación sobre Redis.
 *
 * <p>Esquema de claves:
 * {@code refresh:{tokenId} -> userId} (TTL = vida del refresh token) y
 * {@code user_tokens:{userId} -> Set<tokenId>} para revocación masiva.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(String tokenId, UUID userId, long ttlSeconds) {
        String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;
        String userTokensKey = USER_TOKENS_PREFIX + userId;

        redisTemplate.opsForValue().set(tokenKey, userId.toString(), ttlSeconds, TimeUnit.SECONDS);
        redisTemplate.opsForSet().add(userTokensKey, tokenId);
        redisTemplate.expire(userTokensKey, ttlSeconds, TimeUnit.SECONDS);

        log.debug("Refresh token registrado para usuario: {}", userId);
    }

    @Override
    public Optional<UUID> find(String tokenId) {
        Object value = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + tokenId);
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(value.toString()));
        } catch (IllegalArgumentException e) {
            log.error("Valor corrupto en Redis para refresh token; se trata como revocado.");
            return Optional.empty();
        }
    }

    @Override
    public void delete(String tokenId) {
        String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;
        Object userId = redisTemplate.opsForValue().get(tokenKey);
        if (userId != null) {
            redisTemplate.opsForSet().remove(USER_TOKENS_PREFIX + userId, tokenId);
        }
        redisTemplate.delete(tokenKey);
        log.debug("Refresh token eliminado.");
    }

    @Override
    public void revoke(UUID userId) {
        String userTokensKey = USER_TOKENS_PREFIX + userId;
        Set<Object> tokens = redisTemplate.opsForSet().members(userTokensKey);
        if (tokens != null) {
            for (Object tokenId : tokens) {
                redisTemplate.delete(REFRESH_TOKEN_PREFIX + tokenId);
            }
        }
        redisTemplate.delete(userTokensKey);
        log.info("Todos los refresh tokens revocados para usuario: {}", userId);
    }
}
