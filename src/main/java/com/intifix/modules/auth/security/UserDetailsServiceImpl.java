package com.intifix.modules.auth.security;

import com.intifix.modules.auth.entity.EstadoUsuario;
import com.intifix.modules.auth.entity.RolUsuario;
import com.intifix.modules.auth.entity.UsuarioAuth;
import com.intifix.modules.auth.exception.AccountBannedException;
import com.intifix.modules.auth.exception.AccountSuspendedException;
import com.intifix.modules.auth.exception.UserNotFoundException;
import com.intifix.modules.auth.repository.UsuarioAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioAuthRepository usuarioAuthRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        log.debug("Cargando usuario por correo: {}", correo);

        UsuarioAuth usuario = usuarioAuthRepository.findByCorreo(correo)
            .orElseThrow(() -> {
                log.warn("Usuario no encontrado con correo: {}", correo);
                return UserNotFoundException.byCorreo(correo);
            });

        log.debug("Usuario encontrado: {}, estado: {}", usuario.getCorreo(), usuario.getEstado());

        if (usuario.getEstado() == EstadoUsuario.BANEADO) {
            log.warn("Intento de acceso de usuario baneado: {}", correo);
            throw AccountBannedException.defaultMessage();
        }

        if (usuario.getEstado() == EstadoUsuario.SUSPENDIDO) {
            log.warn("Intento de acceso de usuario suspendido: {}", correo);
            throw AccountSuspendedException.defaultMessage();
        }

        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            log.warn("Usuario con estado no activo: {}, estado: {}", correo, usuario.getEstado());
            throw new AccountSuspendedException("La cuenta no se encuentra activa. Estado: " + usuario.getEstado());
        }

        List<SimpleGrantedAuthority> authorities = usuario.getRoles().stream()
            .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.name()))
            .collect(Collectors.toList());

        log.debug("Autoridades cargadas para usuario {}: {}", correo, authorities);

        return User.builder()
            .username(usuario.getCorreo())
            .password(usuario.getPasswordHash())
            .authorities(authorities)
            .accountLocked(usuario.getEstado() == EstadoUsuario.SUSPENDIDO)
            .disabled(usuario.getEstado() == EstadoUsuario.BANEADO)
            .accountExpired(false)
            .credentialsExpired(false)
            .build();
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(UUID userId) {
        log.debug("Cargando usuario por ID: {}", userId);

        UsuarioAuth usuario = usuarioAuthRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("Usuario no encontrado con ID: {}", userId);
                return UserNotFoundException.byId(userId.toString());
            });

        return loadUserByUsername(usuario.getCorreo());
    }
}
