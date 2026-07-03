package com.intifix.modules.auth.service;

import com.intifix.modules.audit.event.UserCreatedEvent;
import com.intifix.modules.auth.config.AuthProperties;
import com.intifix.modules.auth.dto.*;
import com.intifix.modules.auth.email.PasswordEmailService;
import com.intifix.modules.auth.entity.EstadoUsuario;
import com.intifix.modules.auth.entity.UsuarioAuth;
import com.intifix.modules.auth.exception.*;
import com.intifix.modules.auth.redis.PasswordResetTokenService;
import com.intifix.modules.auth.redis.RefreshTokenService;
import com.intifix.modules.auth.repository.UsuarioAuthRepository;
import com.intifix.modules.auth.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UsuarioAuthRepository usuarioAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final PasswordEmailService passwordEmailService;
    private final AuthProperties authProperties;
    private final ApplicationEventPublisher eventPublisher;

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

        // Auditoría desacoplada: el módulo audit registra el alta de usuario.
        eventPublisher.publishEvent(new UserCreatedEvent(
            usuarioGuardado.getIdUsuario(),
            usuarioGuardado.getCorreo(),
            null,
            String.valueOf(usuarioGuardado.getRoles()),
            request.getDni()
        ));

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
            .telefono(usuario.getTelefono())
            .estado(usuario.getEstado())
            .verificado(usuario.getVerificado())
            .ultimoLogin(usuario.getUltimoLogin())
            .roles(usuario.getRoles())
            .build();
    }

    @Override
    @Transactional
    public UserSessionResponse actualizarTelefono(UUID idUsuario, String telefono) {
        log.info("Actualizando teléfono del usuario: {}", idUsuario);

        UsuarioAuth usuario = usuarioAuthRepository.findById(idUsuario)
            .orElseThrow(() -> UserNotFoundException.byId(idUsuario.toString()));

        if (!telefono.equals(usuario.getTelefono()) && usuarioAuthRepository.existsByTelefono(telefono)) {
            throw UserAlreadyExistsException.byTelefono(telefono);
        }

        usuario.setTelefono(telefono);
        usuarioAuthRepository.save(usuario);

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

    private static final int DIAS_SUSPENSION = 30;
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    @Transactional
    public void cambiarEstadoUsuario(UUID idUsuario, EstadoUsuario nuevoEstado) {
        log.info("Admin cambiando estado de usuario {} a {}", idUsuario, nuevoEstado);
        UsuarioAuth usuario = usuarioAuthRepository.findById(idUsuario)
            .orElseThrow(() -> UserNotFoundException.byId(idUsuario.toString()));
        usuario.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoUsuario.SUSPENDIDO) {
            // Suspensión temporal: se levanta automáticamente a los 30 días.
            usuario.setSuspensionHasta(LocalDateTime.now(ZoneId.systemDefault()).plusDays(DIAS_SUSPENSION));
        } else {
            // ACTIVO o BANEADO: sin fecha de expiración.
            usuario.setSuspensionHasta(null);
        }
        usuarioAuthRepository.save(usuario);
        if (nuevoEstado == EstadoUsuario.SUSPENDIDO || nuevoEstado == EstadoUsuario.BANEADO) {
            refreshTokenService.revoke(idUsuario);
        }
    }

    @Override
    public void forgotPassword(String correo) {
        // Responde siempre 200 para no revelar si el correo existe en la plataforma.
        usuarioAuthRepository.findByCorreo(correo).ifPresent(usuario -> {
            String token = UUID.randomUUID().toString();
            passwordResetTokenService.saveToken(token, usuario.getIdUsuario(), 3600);
            passwordEmailService.enviarRecuperacion(correo, token);
            log.info("Enlace de recuperación generado para usuario: {}", usuario.getIdUsuario());
        });
    }

    @Override
    @Transactional
    public void resetPassword(String token, String nuevaPassword) {
        UUID idUsuario = passwordResetTokenService.findByToken(token)
            .orElseThrow(() -> new InvalidTokenException("El enlace de recuperación no es válido o ha expirado."));

        UsuarioAuth usuario = usuarioAuthRepository.findById(idUsuario)
            .orElseThrow(() -> UserNotFoundException.byId(idUsuario.toString()));

        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuarioAuthRepository.save(usuario);
        passwordResetTokenService.deleteToken(token);
        refreshTokenService.revoke(idUsuario);
        passwordEmailService.enviarConfirmacionCambio(usuario.getCorreo());
        log.info("Contraseña restablecida para usuario: {}", idUsuario);
    }

    @Override
    @Transactional
    public void cambiarPassword(UUID idUsuario, String passwordActual, String nuevaPassword) {
        UsuarioAuth usuario = usuarioAuthRepository.findById(idUsuario)
            .orElseThrow(() -> UserNotFoundException.byId(idUsuario.toString()));

        if (!passwordEncoder.matches(passwordActual, usuario.getPasswordHash())) {
            throw InvalidCredentialsException.defaultMessage();
        }

        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuarioAuthRepository.save(usuario);
        refreshTokenService.revoke(idUsuario);
        passwordEmailService.enviarConfirmacionCambio(usuario.getCorreo());
        log.info("Contraseña cambiada por el usuario: {}", idUsuario);
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
            LocalDateTime hasta = usuario.getSuspensionHasta();
            LocalDateTime ahora = LocalDateTime.now(ZoneId.systemDefault());
            if (hasta != null && ahora.isAfter(hasta)) {
                // Suspensión expirada: reactivar automáticamente.
                log.info("Auto-reactivando usuario {} — suspensión expiró el {}", usuario.getIdUsuario(), hasta.format(FMT_FECHA));
                usuario.setEstado(EstadoUsuario.ACTIVO);
                usuario.setSuspensionHasta(null);
                usuarioAuthRepository.save(usuario);
            } else {
                String fin = (hasta != null) ? " hasta el " + hasta.format(FMT_FECHA) : "";
                throw new AccountSuspendedException("Tu cuenta está suspendida" + fin + ". Contacta al soporte si crees que es un error.");
            }
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
