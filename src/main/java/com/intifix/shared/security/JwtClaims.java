package com.intifix.shared.security;

import java.util.UUID;

public record JwtClaims(
        UUID userId,
        String role,
        String tokenType,
        String jti
) {
}

