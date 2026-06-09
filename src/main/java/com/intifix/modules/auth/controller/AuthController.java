package com.intifix.modules.auth.controller;

import com.intifix.modules.auth.dto.LoginRequest;
import com.intifix.modules.auth.dto.RegisterRequest;
import com.intifix.modules.auth.dto.RefreshRequest;
import com.intifix.modules.auth.dto.AuthResponse;
import com.intifix.modules.auth.service.AuthService;
import com.intifix.shared.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity < ApiResponse < AuthResponse >> registrar(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.registrar(request);
        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success("Usuario registrado e iniciado sesión de forma soplete.", response));
    }

    @PostMapping("/login")
    public ResponseEntity < ApiResponse < AuthResponse >> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Acceso concedido, bienvenido a INTIFIX.", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity < ApiResponse < AuthResponse >> refrescar(@RequestBody RefreshRequest request) {
        AuthResponse response = authService.refrescarSesion(request);
        return ResponseEntity.ok(ApiResponse.success("Token renovado con éxito, mi rey.", response));
    }
}
