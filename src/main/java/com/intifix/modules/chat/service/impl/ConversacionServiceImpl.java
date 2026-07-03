package com.intifix.modules.chat.service.impl;

import com.intifix.modules.chat.dto.request.CrearConversacionRequest;
import com.intifix.modules.chat.dto.request.CrearConsultaRequest;
import com.intifix.modules.chat.dto.response.ConversacionResponse;
import com.intifix.modules.chat.entity.ConversacionDocument;
import com.intifix.modules.chat.entity.EstadoConversacion;
import com.intifix.modules.chat.entity.TipoConversacion;
import com.intifix.modules.chat.exception.ConversacionDuplicadaException;
import com.intifix.modules.chat.exception.ConversacionNoEncontradaException;
import com.intifix.modules.chat.exception.ServicioInvalidoException;
import com.intifix.modules.chat.exception.UsuarioNoParticipanteException;
import com.intifix.modules.chat.gateway.ChatGateway;
import com.intifix.modules.chat.mapper.ConversacionMapper;
import com.intifix.modules.chat.repository.ConversacionRepository;
import com.intifix.modules.chat.repository.MensajeRepository;
import com.intifix.modules.chat.service.ConversacionService;
import com.intifix.shared.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversacionServiceImpl implements ConversacionService {

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final ConversacionMapper conversacionMapper;
    private final ChatGateway chatGateway;

    @Override
    public ConversacionResponse crear(CrearConversacionRequest request) {
        UUID solicitante = SecurityUtils.currentUserId();
        UUID idServicio = request.getIdServicio();
        log.info("Creando conversación para servicio {} por usuario {}", idServicio, solicitante);

        ChatGateway.ServicioParticipantes participantes = chatGateway.obtenerParticipantes(idServicio)
                .orElseThrow(() -> ServicioInvalidoException.noEncontrado(idServicio));

        if (participantes.idTecnico() == null) {
            throw ServicioInvalidoException.sinTecnico(idServicio);
        }

        // Solo el cliente o el técnico del servicio pueden abrir la conversación.
        if (!solicitante.equals(participantes.idCliente()) && !solicitante.equals(participantes.idTecnico())) {
            throw UsuarioNoParticipanteException.porDefecto();
        }

        // Una conversación por servicio: si ya existe se devuelve (idempotente),
        // salvo carrera con el índice único que protege a nivel de BD.
        if (conversacionRepository.existsByIdServicio(idServicio)) {
            throw ConversacionDuplicadaException.porServicio(idServicio);
        }

        ConversacionDocument doc = ConversacionDocument.builder()
                .id(UUID.randomUUID())
                .idServicio(idServicio)
                .idCliente(participantes.idCliente())
                .idTecnico(participantes.idTecnico())
                .estado(EstadoConversacion.ACTIVA)
                .noLeidosCliente(0)
                .noLeidosTecnico(0)
                .build();

        ConversacionDocument guardada = conversacionRepository.save(doc);
        log.info("Conversación creada: {}", guardada.getId());
        return aResponse(guardada, solicitante);
    }

    @Override
    public Page<ConversacionResponse> misConversaciones(Pageable pageable) {
        UUID userId = SecurityUtils.currentUserId();
        return conversacionRepository.findByIdClienteOrIdTecnico(userId, userId, pageable)
                .map(doc -> aResponse(doc, userId));
    }

    @Override
    public ConversacionResponse obtenerPorId(UUID idConversacion) {
        UUID userId = SecurityUtils.currentUserId();
        ConversacionDocument doc = cargarParticipando(idConversacion, userId);
        return aResponse(doc, userId);
    }

    @Override
    public void archivar(UUID idConversacion) {
        UUID userId = SecurityUtils.currentUserId();
        ConversacionDocument doc = cargarParticipando(idConversacion, userId);
        doc.setEstado(EstadoConversacion.ARCHIVADA);
        conversacionRepository.save(doc);
        log.info("Conversación archivada: {}", idConversacion);
    }

    @Override
    public void desarchivar(UUID idConversacion) {
        UUID userId = SecurityUtils.currentUserId();
        ConversacionDocument doc = cargarParticipando(idConversacion, userId);
        doc.setEstado(EstadoConversacion.ACTIVA);
        conversacionRepository.save(doc);
        log.info("Conversación desarchivada: {}", idConversacion);
    }

    @Override
    public void bloquear(UUID idConversacion) {
        UUID userId = SecurityUtils.currentUserId();
        ConversacionDocument doc = cargarParticipando(idConversacion, userId);
        doc.setEstado(EstadoConversacion.BLOQUEADA);
        doc.setBloqueadaPor(userId);
        conversacionRepository.save(doc);
        log.info("Conversación {} bloqueada por {}", idConversacion, userId);
    }

    @Override
    public void desbloquear(UUID idConversacion) {
        UUID userId = SecurityUtils.currentUserId();
        ConversacionDocument doc = cargarParticipando(idConversacion, userId);
        doc.setEstado(EstadoConversacion.ACTIVA);
        doc.setBloqueadaPor(null);
        conversacionRepository.save(doc);
        log.info("Conversación {} desbloqueada por {}", idConversacion, userId);
    }

    @Override
    public void eliminar(UUID idConversacion) {
        UUID userId = SecurityUtils.currentUserId();
        ConversacionDocument doc = cargarParticipando(idConversacion, userId);
        mensajeRepository.deleteByIdConversacion(idConversacion);
        conversacionRepository.delete(doc);
        log.info("Conversación {} eliminada por {}", idConversacion, userId);
    }

    @Override
    public ConversacionResponse crearConsulta(CrearConsultaRequest request) {
        UUID idCliente = SecurityUtils.currentUserId();
        UUID idTecnico = request.getIdTecnico();
        log.info("Creando consulta entre cliente {} y técnico {}", idCliente, idTecnico);

        if (!chatGateway.existeUsuario(idTecnico)) {
            throw new IllegalArgumentException("El técnico indicado no existe.");
        }

        // Idempotente: si ya existe la devolvemos en lugar de lanzar 409.
        return conversacionRepository.findByIdClienteAndIdTecnicoAndTipo(idCliente, idTecnico, TipoConversacion.CONSULTA)
                .map(doc -> aResponse(doc, idCliente))
                .orElseGet(() -> {
                    ConversacionDocument doc = ConversacionDocument.builder()
                            .id(UUID.randomUUID())
                            .idServicio(null)
                            .idCliente(idCliente)
                            .idTecnico(idTecnico)
                            .tipo(TipoConversacion.CONSULTA)
                            .estado(EstadoConversacion.ACTIVA)
                            .noLeidosCliente(0)
                            .noLeidosTecnico(0)
                            .build();
                    ConversacionDocument guardada = conversacionRepository.save(doc);
                    log.info("Consulta creada: {}", guardada.getId());
                    return aResponse(guardada, idCliente);
                });
    }

    @Override
    public ConversacionDocument cargarParticipando(UUID idConversacion, UUID idUsuario) {
        ConversacionDocument doc = conversacionRepository.findById(idConversacion)
                .orElseThrow(() -> ConversacionNoEncontradaException.porId(idConversacion));
        if (!esParticipante(doc, idUsuario)) {
            log.warn("Usuario {} intentó acceder a conversación ajena {}", idUsuario, idConversacion);
            throw UsuarioNoParticipanteException.porDefecto();
        }
        return doc;
    }

    private boolean esParticipante(ConversacionDocument doc, UUID userId) {
        return userId.equals(doc.getIdCliente()) || userId.equals(doc.getIdTecnico());
    }

    private ConversacionResponse aResponse(ConversacionDocument doc, UUID userId) {
        ConversacionResponse response = conversacionMapper.toResponse(doc);
        // No leídos del usuario que consulta (denormalizado por participante).
        response.setNoLeidos(userId.equals(doc.getIdCliente())
                ? doc.getNoLeidosCliente()
                : doc.getNoLeidosTecnico());
        return response;
    }
}
