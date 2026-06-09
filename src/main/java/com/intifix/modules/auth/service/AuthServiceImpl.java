package com.intifix.modules.auth.service;

import com.intifix.modules.auth.dto.AuthResponse;
import com.intifix.modules.auth.dto.LoginRequest;
import com.intifix.modules.auth.dto.RefreshRequest;
import com.intifix.modules.auth.dto.RegisterRequest;
import com.intifix.modules.auth.util.JwtTokenProvider;
import com.intifix.modules.users.entity.Usuario;
import com.intifix.modules.users.service.UsuarioService;
import com.intifix.shared.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserDetailsService userDetailsService;
    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        Usuario nuevoUsuario = Usuario.builder()
            .correo(request.getCorreo())
            .clave(request.getClave())
            .telefono(request.getTelefono())
            .roles(request.getRoles())
            .build();

        Usuario registrado = usuarioService.registrarUsuario(
            nuevoUsuario,
            request.getNombresCompletos(),
            request.getDniRuc(),
            request.getDniFrontalUrl(),
            request.getDniTraseroUrl(),
            request.getAntecedentePenalUrl(),
            request.getCertificadoTecnicoUrl(),
            request.getExperienciaAnios(),
            request.getTarifaBase()
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(registrado.getCorreo());

        return AuthResponse.builder()
            .accessToken(jwtTokenProvider.generarAccessToken(userDetails.getUsername(), userDetails.getAuthorities()))
            .refreshToken(jwtTokenProvider.generarRefreshToken(userDetails.getUsername()))
            .correo(registrado.getCorreo())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getCorreo());

        if (!passwordEncoder.matches(request.getClave(), userDetails.getPassword())) {
            throw new CustomException("Credenciales incorrectas.");
        }

        if (!userDetails.isEnabled()) {
            throw new CustomException("Esta cuenta no se encuentra activa o fue bloqueada de INTIFIX.");
        }

        return AuthResponse.builder()
            .accessToken(jwtTokenProvider.generarAccessToken(userDetails.getUsername(), userDetails.getAuthorities()))
            .refreshToken(jwtTokenProvider.generarRefreshToken(userDetails.getUsername()))
            .correo(userDetails.getUsername())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refrescarSesion(RefreshRequest request) {
        String tokenRefresco = request.getRefreshToken();

        if (tokenRefresco == null || !jwtTokenProvider.validarToken(tokenRefresco)) {
            throw new CustomException("Sesion expirada o invalida. Vuelve a iniciar sesion.");
        }

        String correo = jwtTokenProvider.obtenerCorreo(tokenRefresco);
        UserDetails userDetails = userDetailsService.loadUserByUsername(correo);

        if (!userDetails.isEnabled()) {
            throw new CustomException("Cuenta inhabilitada.");
        }

        return AuthResponse.builder()
            .accessToken(jwtTokenProvider.generarAccessToken(userDetails.getUsername(), userDetails.getAuthorities()))
            .refreshToken(tokenRefresco)
            .correo(userDetails.getUsername())
            .build();
    }
}
