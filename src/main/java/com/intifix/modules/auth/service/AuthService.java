package com.intifix.modules.auth.service;

import com.intifix.modules.auth.dto.*;
import com.intifix.modules.auth.entity.EstadoUsuario;

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

    /** Actualiza el teléfono del usuario autenticado. */
    UserSessionResponse actualizarTelefono(UUID idUsuario, String telefono);

    /** Cambia el estado de cuenta de un usuario (ADMIN). */
    void cambiarEstadoUsuario(UUID idUsuario, EstadoUsuario nuevoEstado);

    /** Inicia el flujo de recuperación de contraseña (envía email con token). */
    void forgotPassword(String correo);

    /** Restablece la contraseña usando el token del email de recuperación. */
    void resetPassword(String token, String nuevaPassword);

    /** Cambia la contraseña del usuario autenticado (requiere la actual). */
    void cambiarPassword(UUID idUsuario, String passwordActual, String nuevaPassword);
}
