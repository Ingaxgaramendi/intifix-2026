package com.intifix.modules.auth.redis;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenService {
    void saveToken(String token, UUID userId, long ttlSeconds);
    Optional<UUID> findByToken(String token);
    void deleteToken(String token);
}
