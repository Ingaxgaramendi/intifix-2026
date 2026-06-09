package com.intifix.modules.auth.service;

import com.intifix.modules.auth.dto.LoginRequest;
import com.intifix.modules.auth.dto.RegisterRequest;
import com.intifix.modules.auth.dto.RefreshRequest;
import com.intifix.modules.auth.dto.AuthResponse;

public interface AuthService {
    AuthResponse registrar(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refrescarSesion(RefreshRequest request);
}
