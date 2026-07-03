package com.intifix.modules.apelaciones.service;

import com.intifix.modules.apelaciones.dto.ApelacionResponse;
import com.intifix.modules.apelaciones.dto.CrearApelacionRequest;
import com.intifix.modules.apelaciones.dto.RevisarApelacionRequest;
import com.intifix.modules.apelaciones.entity.Apelacion;
import com.intifix.modules.apelaciones.entity.EstadoApelacion;
import com.intifix.modules.apelaciones.entity.TipoApelacion;
import com.intifix.modules.apelaciones.repository.ApelacionRepository;
import com.intifix.modules.auth.entity.EstadoUsuario;
import com.intifix.modules.auth.entity.UsuarioAuth;
import com.intifix.modules.auth.repository.UsuarioAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApelacionServiceImpl implements ApelacionService {

    private final ApelacionRepository apelacionRepository;
    private final UsuarioAuthRepository usuarioAuthRepository;

    @Override
    @Transactional
    public void crear(CrearApelacionRequest request) {
        // Detect tipo from the actual account state. If the user doesn't exist
        // or is ACTIVO, we still accept the request silently (no info leak).
        TipoApelacion tipo = TipoApelacion.SUSPENSION; // default
        var optUsuario = usuarioAuthRepository.findByCorreo(request.getCorreo());
        if (optUsuario.isPresent()) {
            UsuarioAuth u = optUsuario.get();
            if (u.getEstado() == EstadoUsuario.BANEADO) tipo = TipoApelacion.BAN;
        }

        Apelacion apelacion = Apelacion.builder()
            .correo(request.getCorreo().toLowerCase().strip())
            .tipo(tipo)
            .mensaje(request.getMensaje().strip())
            .estado(EstadoApelacion.PENDIENTE)
            .build();

        apelacionRepository.save(apelacion);
        log.info("Apelación recibida de {}: tipo={}", request.getCorreo(), tipo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApelacionResponse> listar(EstadoApelacion estado, Pageable pageable) {
        Page<Apelacion> page = (estado != null)
            ? apelacionRepository.findByEstadoOrderByFechaEnvioDesc(estado, pageable)
            : apelacionRepository.findAllByOrderByFechaEnvioDesc(pageable);
        return page.map(this::toResponse);
    }

    @Override
    @Transactional
    public ApelacionResponse revisar(UUID idApelacion, RevisarApelacionRequest request) {
        Apelacion apelacion = apelacionRepository.findById(idApelacion)
            .orElseThrow(() -> new IllegalArgumentException("Apelación no encontrada: " + idApelacion));
        apelacion.setEstado(request.getEstado());
        if (request.getNotaAdmin() != null) {
            apelacion.setNotaAdmin(request.getNotaAdmin().strip());
        }
        return toResponse(apelacionRepository.save(apelacion));
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPendientes() {
        return apelacionRepository.countByEstado(EstadoApelacion.PENDIENTE);
    }

    private ApelacionResponse toResponse(Apelacion a) {
        return ApelacionResponse.builder()
            .idApelacion(a.getIdApelacion())
            .correo(a.getCorreo())
            .tipo(a.getTipo())
            .mensaje(a.getMensaje())
            .estado(a.getEstado())
            .notaAdmin(a.getNotaAdmin())
            .fechaEnvio(a.getFechaEnvio())
            .build();
    }
}
