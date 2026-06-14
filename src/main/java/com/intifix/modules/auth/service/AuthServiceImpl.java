package com.intifix.modules.auth.service;

import com.intifix.modules.auth.config.AuthProperties;
import com.intifix.modules.auth.dto.*;
import com.intifix.modules.auth.entity.EstadoUsuario;
import com.intifix.modules.auth.entity.UsuarioAuth;
import com.intifix.modules.auth.exception.*;
import com.intifix.modules.auth.redis.RefreshTokenService;
import com.intifix.modules.auth.repository.UsuarioAuthRepository;
import com.intifix.modules.auth.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UsuarioAuthRepository usuarioAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuthProperties authProperties;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Iniciando registro de usuario: {}", request.getCorreo());

        if (usuarioAuthRepository.existsByCorreo(request.getCorreo())) {
            log.warn("Intento de registro con correo ya existente: {}", request.getCorreo());
            throw UserAlreadyExistsException.byCorreo(request.getCorreo());
        }

        if (usuarioAuthRepository.existsByTelefono(request.getTelefono())) {
            log.warn("Intento de registro con teléfono ya existente");
            throw UserAlreadyExistsException.byTelefono(request.getTelefono());
        }

        UsuarioAuth nuevoUsuario = UsuarioAuth.builder()
            .correo(request.getCorreo())
            .passwordHash(passwordEncoder.encode(request.getClave()))
            .telefono(request.getTelefono())
            .roles(request.getRoles())
            .estado(EstadoUsuario.ACTIVO)
            .verificado(false)
            .intentosFallidos(0)
            .fechaRegistro(LocalDateTime.now())
            .build();

        UsuarioAuth usuarioGuardado = usuarioAuthRepository.save(nuevoUsuario);
        log.info("Usuario registrado exitosamente: {}", usuarioGuardado.getIdUsuario());

        return generateAuthResponse(usuarioGuardado);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getCorreo());

        UsuarioAuth usuario = usuarioAuthRepository.findByCorreo(request.getCorreo())
            .orElseThrow(() -> {
                log.warn("Login fallido - usuario no encontrado: {}", request.getCorreo());
                return InvalidCredentialsException.defaultMessage();
            });

        verificarEstadoCuenta(usuario);

        if (!passwordEncoder.matches(request.getClave(), usuario.getPasswordHash())) {
            log.warn("Credenciales inválidas para usuario: {}", usuario.getIdUsuario());
            incrementarIntentosFallidos(usuario);
            throw InvalidCredentialsException.defaultMessage();
        }

        reiniciarIntentosFallidos(usuario);
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioAuthRepository.save(usuario);

        log.info("Login exitoso para usuario: {}", usuario.getIdUsuario());
        return generateAuthResponse(usuario);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshRequest request) {
        log.debug("Intento de refresh token");

        String refreshToken = request.getRefreshToken();
        jwtTokenProvider.validarToken(refreshToken);

        Claims claims = jwtTokenProvider.extraerClaims(refreshToken);
        if (!JwtTokenProvider.TOKEN_TYPE_REFRESH.equals(jwtTokenProvider.obtenerTipo(claims))) {
            log.warn("Se intentó refrescar sesión con un token que no es refresh");
            throw InvalidTokenException.unsupported();
        }

        String tokenId = claims.getId();
        UUID userId = refreshTokenService.find(tokenId)
            .orElseThrow(() -> {
                log.warn("Refresh token no encontrado en Redis (revocado o expirado)");
                return RefreshTokenExpiredException.defaultMessage();
            });

        UsuarioAuth usuario = usuarioAuthRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("Usuario no encontrado al refrescar token: {}", userId);
                return UserNotFoundException.byId(userId.toString());
            });

        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            log.warn("Refresh denegado por estado {} para usuario: {}", usuario.getEstado(), userId);
            // Cuenta comprometida o sancionada: se revocan TODAS sus sesiones.
            refreshTokenService.revoke(userId);
            verificarEstadoCuenta(usuario);
        }

        // Rotación de refresh token: el usado se invalida y se emite uno nuevo.
        refreshTokenService.delete(tokenId);

        log.info("Token refrescado exitosamente para usuario: {}", userId);
        return generateAuthResponse(usuario);
    }

    @Override
    public void logout(LogoutRequest request) {
        log.debug("Intento de logout");

        String refreshToken = request.getRefreshToken();
        try {
            jwtTokenProvider.validarToken(refreshToken);
        } catch (InvalidTokenException e) {
            // Logout es idempotente: un token inválido ya no representa sesión.
            log.debug("Logout con token inválido: {}", e.getMessage());
            return;
        }

        refreshTokenService.delete(jwtTokenProvider.obtenerTokenId(refreshToken));
        log.info("Logout exitoso.");
    }

    @Override
    @Transactional(readOnly = true)
    public UserSessionResponse validateSession(String token) {
        log.debug("Validando sesión");

        jwtTokenProvider.validarToken(token);
        UUID userId = jwtTokenProvider.obtenerUserId(token);

        UsuarioAuth usuario = usuarioAuthRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.byId(userId.toString()));

        return UserSessionResponse.builder()
            .idUsuario(usuario.getIdUsuario())
            .correo(usuario.getCorreo())
            .telefono(usuario.getTelefono())
            .estado(usuario.getEstado())
            .verificado(usuario.getVerificado())
            .intentosFallidos(usuario.getIntentosFallidos())
            .ultimoLogin(usuario.getUltimoLogin())
            .fechaRegistro(usuario.getFechaRegistro())
            .roles(usuario.getRoles())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser(UUID idUsuario) {
        log.debug("Obteniendo usuario actual: {}", idUsuario);

        UsuarioAuth usuario = usuarioAuthRepository.findById(idUsuario)
            .orElseThrow(() -> UserNotFoundException.byId(idUsuario.toString()));

        return CurrentUserResponse.builder()
            .idUsuario(usuario.getIdUsuario())
            .correo(usuario.getCorreo())
            .estado(usuario.getEstado())
            .verificado(usuario.getVerificado())
            .ultimoLogin(usuario.getUltimoLogin())
            .roles(usuario.getRoles())
            .build();
    }

    private AuthResponse generateAuthResponse(UsuarioAuth usuario) {
        String accessToken = jwtTokenProvider.generarAccessToken(
            usuario.getIdUsuario(),
            usuario.getCorreo(),
            usuario.getRoles()
        );

        String refreshToken = jwtTokenProvider.generarRefreshToken(usuario.getIdUsuario());

        refreshTokenService.save(
            jwtTokenProvider.obtenerTokenId(refreshToken),
            usuario.getIdUsuario(),
            jwtTokenProvider.getRefreshTokenExpiration()
        );

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
            .correo(usuario.getCorreo())
            .build();
    }

    private void verificarEstadoCuenta(UsuarioAuth usuario) {
        if (usuario.getEstado() == EstadoUsuario.BANEADO) {
            throw AccountBannedException.defaultMessage();
        }
        if (usuario.getEstado() == EstadoUsuario.SUSPENDIDO) {
            throw AccountSuspendedException.defaultMessage();
        }
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new AccountSuspendedException("La cuenta no se encuentra activa.");
        }
    }

    private void incrementarIntentosFallidos(UsuarioAuth usuario) {
        usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);

        if (usuario.getIntentosFallidos() >= authProperties.getAuth().getMaxFailedAttempts()) {
            log.warn("Usuario suspendido por exceder intentos fallidos: {}, intentos: {}",
                usuario.getIdUsuario(), usuario.getIntentosFallidos());
            usuario.setEstado(EstadoUsuario.SUSPENDIDO);
            // Sesiones activas dejan de poder renovarse.
            refreshTokenService.revoke(usuario.getIdUsuario());
        }

        usuarioAuthRepository.save(usuario);
    }

    private void reiniciarIntentosFallidos(UsuarioAuth usuario) {
        if (usuario.getIntentosFallidos() > 0) {
            usuario.setIntentosFallidos(0);
            usuarioAuthRepository.save(usuario);
        }
    }
}
