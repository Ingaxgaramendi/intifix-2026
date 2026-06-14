package com.intifix.modules.auth.controller;

import com.intifix.modules.auth.dto.*;
import com.intifix.modules.auth.service.AuthService;
import com.intifix.shared.api.ApiResponse;
import com.intifix.shared.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
