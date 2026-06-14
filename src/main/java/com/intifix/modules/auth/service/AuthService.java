package com.intifix.modules.auth.service;

import com.intifix.modules.auth.dto.*;

import java.util.UUID;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshRequest request);

    void logout(LogoutRequest request);

    UserSessionResponse validateSession(String token);

    /**
     * El usuario actual se identifica por su UUID (el principal autenticado),
     * no por correo: el correo es un atributo mutable, no una identidad.
     */
    CurrentUserResponse getCurrentUser(UUID idUsuario);
}
