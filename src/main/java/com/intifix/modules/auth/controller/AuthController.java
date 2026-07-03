package com.intifix.modules.auth.controller;

import com.intifix.modules.auth.dto.*;
import com.intifix.modules.auth.entity.EstadoUsuario;
import com.intifix.modules.auth.service.AuthService;
import com.intifix.shared.api.ApiResponse;
import com.intifix.shared.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Usuario registrado exitosamente.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login exitoso. Bienvenido.", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token renovado exitosamente.", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success("Logout exitoso.", null));
    }

    @GetMapping("/validate-session")
    public ResponseEntity<ApiResponse<UserSessionResponse>> validateSession(
            @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        UserSessionResponse response = authService.validateSession(token);
        return ResponseEntity.ok(ApiResponse.success("Sesión válida.", response));
    }

    @GetMapping("/current-user")
    public ResponseEntity<ApiResponse<CurrentUserResponse>> getCurrentUser(
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        CurrentUserResponse response = authService.getCurrentUser(usuario.getId());
        return ResponseEntity.ok(ApiResponse.success("Usuario actual recuperado.", response));
    }

    @PatchMapping("/usuarios/{idUsuario}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cambiarEstadoUsuario(
            @PathVariable java.util.UUID idUsuario,
            @jakarta.validation.Valid @RequestBody CambiarEstadoUsuarioRequest request) {
        authService.cambiarEstadoUsuario(idUsuario, request.getEstado());
        return ResponseEntity.ok(ApiResponse.success("Estado del usuario actualizado.", null));
    }

    @PatchMapping("/me/telefono")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserSessionResponse>> actualizarTelefono(
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody ActualizarTelefonoRequest request) {
        UserSessionResponse response = authService.actualizarTelefono(usuario.getId(), request.getTelefono());
        return ResponseEntity.ok(ApiResponse.success("Teléfono actualizado.", response));
    }

    @PostMapping("/password/forgot")
    @Operation(summary = "Solicitar recuperación de contraseña", description = "Envía un email con enlace de recuperación. Siempre responde 200 para no revelar si el correo existe.")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getCorreo());
        return ResponseEntity.ok(ApiResponse.success(
            "Si ese correo está registrado, recibirás un enlace en los próximos minutos.", null));
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Restablecer contraseña con token de email")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNuevaPassword());
        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada correctamente. Ya puedes iniciar sesión.", null));
    }

    @PatchMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cambiar contraseña del usuario autenticado")
    public ResponseEntity<ApiResponse<Void>> cambiarPassword(
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody CambiarPasswordRequest request) {
        authService.cambiarPassword(usuario.getId(), request.getPasswordActual(), request.getNuevaPassword());
        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada. Revisa tu correo.", null));
    }
}
