package com.intifix.modules.chat.service.impl;

import com.intifix.modules.chat.dto.request.AdjuntoRequest;
import com.intifix.modules.chat.dto.request.EditarMensajeRequest;
import com.intifix.modules.chat.dto.request.EnviarMensajeRequest;
import com.intifix.modules.chat.dto.response.MensajeResponse;
import com.intifix.modules.chat.entity.ConversacionDocument;
import com.intifix.modules.chat.entity.EstadoConversacion;
import com.intifix.modules.chat.entity.EstadoMensaje;
import com.intifix.modules.chat.entity.MensajeDocument;
import com.intifix.modules.chat.entity.TipoMensaje;
import com.intifix.modules.chat.event.MensajeEnviadoEvent;
import com.intifix.modules.audit.event.ChatMessageSentEvent;
import com.intifix.modules.chat.exception.ArchivoInvalidoException;
import com.intifix.modules.chat.exception.ConversacionBloqueadaException;
import com.intifix.modules.chat.exception.MensajeNoEncontradoException;
import com.intifix.modules.chat.mapper.MensajeMapper;
import com.intifix.modules.chat.repository.MensajeRepository;
import com.intifix.modules.chat.service.ConversacionService;
import com.intifix.modules.chat.service.MensajeService;
import com.intifix.shared.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MensajeServiceImpl implements MensajeService {

    private final MensajeRepository mensajeRepository;
    private final ConversacionService conversacionService;
    private final MensajeMapper mensajeMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final MongoTemplate mongoTemplate;

    @Override
    public MensajeResponse enviar(EnviarMensajeRequest request) {
        UUID emisor = SecurityUtils.currentUserId();
        ConversacionDocument conv = conversacionService.cargarParticipando(request.getIdConversacion(), emisor);

        if (conv.getEstado() == EstadoConversacion.BLOQUEADA) {
            throw ConversacionBloqueadaException.porDefecto();
        }

        validarContenido(request);
        validarRespuesta(request, conv.getId());

        MensajeDocument mensaje = MensajeDocument.builder()
                .id(UUID.randomUUID())
                .idConversacion(conv.getId())
                .idEmisor(emisor)
                .tipo(request.getTipo())
                .estado(EstadoMensaje.ENVIADO)
                .contenido(request.getContenido())
                .adjunto(mapearAdjunto(request.getAdjunto()))
                .idMensajeRespondido(request.getIdMensajeRespondido())
                .editado(false)
                .eliminado(false)
                .build();

        MensajeDocument guardado = mensajeRepository.save(mensaje);
        actualizarConversacionTrasMensaje(conv, guardado, emisor);

        UUID destinatario = emisor.equals(conv.getIdCliente()) ? conv.getIdTecnico() : conv.getIdCliente();
        MensajeResponse response = mensajeMapper.toResponse(guardado);

        eventPublisher.publishEvent(new MensajeEnviadoEvent(conv.getId(), emisor, destinatario, response));
        // Auditoría de actividad WebSocket (websocket_logs), desacoplada.
        eventPublisher.publishEvent(new ChatMessageSentEvent(conv.getId(), emisor, destinatario, guardado.getId()));
        log.info("Mensaje {} enviado en conversación {}", guardado.getId(), conv.getId());
        return response;
    }

    @Override
    public MensajeResponse editar(UUID idMensaje, EditarMensajeRequest request) {
        UUID userId = SecurityUtils.currentUserId();
        MensajeDocument mensaje = cargarMensaje(idMensaje);
        verificarEmisor(mensaje, userId);

        if (mensaje.getTipo() != TipoMensaje.TEXTO) {
            throw new ArchivoInvalidoException("Solo se pueden editar mensajes de texto");
        }

        mensaje.setContenido(request.getContenido());
        mensaje.setEditado(true);
        MensajeDocument actualizado = mensajeRepository.save(mensaje);
        log.info("Mensaje {} editado", idMensaje);
        return mensajeMapper.toResponse(actualizado);
    }

    @Override
    public void eliminar(UUID idMensaje) {
        UUID userId = SecurityUtils.currentUserId();
        MensajeDocument mensaje = cargarMensaje(idMensaje);
        verificarEmisor(mensaje, userId);

        // Soft delete: se conserva el documento (auditoría) sin el contenido.
        mensaje.setEliminado(true);
        mensaje.setContenido(null);
        mensaje.setAdjunto(null);
        mensajeRepository.save(mensaje);
        log.info("Mensaje {} eliminado (soft)", idMensaje);
    }

    @Override
    public Page<MensajeResponse> historial(UUID idConversacion, Pageable pageable) {
        UUID userId = SecurityUtils.currentUserId();
        conversacionService.cargarParticipando(idConversacion, userId);
        return mensajeRepository.findByIdConversacion(idConversacion, pageable)
                .map(mensajeMapper::toResponse);
    }

    @Override
    public long marcarLeida(UUID idConversacion) {
        UUID userId = SecurityUtils.currentUserId();
        ConversacionDocument conv = conversacionService.cargarParticipando(idConversacion, userId);

        List<MensajeDocument> pendientes = mensajeRepository
                .findByIdConversacionAndIdEmisorNotAndEstadoNot(idConversacion, userId, EstadoMensaje.LEIDO);

        Instant ahora = Instant.now();
        pendientes.forEach(m -> {
            m.setEstado(EstadoMensaje.LEIDO);
            m.setLeidoEn(ahora);
        });
        mensajeRepository.saveAll(pendientes);

        // Resetea el contador de no leídos del usuario que leyó con un update
        // ATÓMICO (no save de la entidad): así no choca con el @Version del
        // documento cuando ambos participantes escriben/leen a la vez.
        String campo = userId.equals(conv.getIdCliente()) ? "noLeidosCliente" : "noLeidosTecnico";
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(idConversacion)),
                new Update().set(campo, 0),
                ConversacionDocument.class);

        log.info("Usuario {} marcó {} mensajes como leídos en {}", userId, pendientes.size(), idConversacion);
        return pendientes.size();
    }

    @Override
    public long marcarRecibida(UUID idConversacion) {
        UUID userId = SecurityUtils.currentUserId();
        conversacionService.cargarParticipando(idConversacion, userId);

        // Solo los que aún están en ENVIADO (no tocar los ya LEIDO/RECIBIDO).
        List<MensajeDocument> pendientes = mensajeRepository
                .findByIdConversacionAndIdEmisorNotAndEstado(idConversacion, userId, EstadoMensaje.ENVIADO);
        if (pendientes.isEmpty()) {
            return 0;
        }

        pendientes.forEach(m -> m.setEstado(EstadoMensaje.RECIBIDO));
        mensajeRepository.saveAll(pendientes);
        // Nota: NO se toca el documento de conversación → sin optimistic locking.
        log.info("Usuario {} marcó {} mensajes como entregados en {}", userId, pendientes.size(), idConversacion);
        return pendientes.size();
    }

    @Override
    public long contarNoLeidos(UUID idConversacion) {
        UUID userId = SecurityUtils.currentUserId();
        conversacionService.cargarParticipando(idConversacion, userId);
        return mensajeRepository.countByIdConversacionAndIdEmisorNotAndEstadoNot(
                idConversacion, userId, EstadoMensaje.LEIDO);
    }

    // ---------------------------------------------------------------- helpers

    private MensajeDocument cargarMensaje(UUID idMensaje) {
        MensajeDocument mensaje = mensajeRepository.findById(idMensaje)
                .orElseThrow(() -> MensajeNoEncontradoException.porId(idMensaje));
        if (mensaje.isEliminado()) {
            throw MensajeNoEncontradoException.porId(idMensaje);
        }
        return mensaje;
    }

    private void verificarEmisor(MensajeDocument mensaje, UUID userId) {
        if (!userId.equals(mensaje.getIdEmisor())) {
            throw new AccessDeniedException("No puede operar sobre un mensaje ajeno");
        }
    }

    private void validarContenido(EnviarMensajeRequest request) {
        if (request.getTipo() == TipoMensaje.TEXTO) {
            if (request.getContenido() == null || request.getContenido().isBlank()) {
                throw new ArchivoInvalidoException("El contenido es obligatorio para mensajes de texto");
            }
        } else {
            AdjuntoRequest adjunto = request.getAdjunto();
            if (adjunto == null || adjunto.getUrl() == null || adjunto.getUrl().isBlank()) {
                throw new ArchivoInvalidoException("El adjunto (URL) es obligatorio para mensajes de tipo " + request.getTipo());
            }
        }
    }

    private void validarRespuesta(EnviarMensajeRequest request, UUID idConversacion) {
        UUID respondido = request.getIdMensajeRespondido();
        if (respondido == null) {
            return;
        }
        MensajeDocument original = mensajeRepository.findById(respondido)
                .orElseThrow(() -> MensajeNoEncontradoException.porId(respondido));
        if (!idConversacion.equals(original.getIdConversacion())) {
            throw new ArchivoInvalidoException("El mensaje respondido no pertenece a esta conversación");
        }
    }

    private MensajeDocument.Adjunto mapearAdjunto(AdjuntoRequest req) {
        if (req == null) {
            return null;
        }
        return MensajeDocument.Adjunto.builder()
                .url(req.getUrl())
                .nombreArchivo(req.getNombreArchivo())
                .tipoMime(req.getTipoMime())
                .tamanoBytes(req.getTamanoBytes())
                .build();
    }

    private void actualizarConversacionTrasMensaje(ConversacionDocument conv, MensajeDocument mensaje, UUID emisor) {
        ConversacionDocument.UltimoMensaje ultimo = ConversacionDocument.UltimoMensaje.builder()
                .idMensaje(mensaje.getId())
                .idEmisor(emisor)
                .tipo(mensaje.getTipo())
                .preview(generarPreview(mensaje))
                .fecha(Instant.now())
                .build();

        // Update ATÓMICO: denormaliza el último mensaje, incrementa el no leído
        // del receptor y refresca actualizadoEn (orden del inbox). Evita el
        // save de la entidad completa y, con él, los conflictos de @Version.
        String campoNoLeido = emisor.equals(conv.getIdCliente()) ? "noLeidosTecnico" : "noLeidosCliente";
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(conv.getId())),
                new Update()
                        .set("ultimoMensaje", ultimo)
                        .inc(campoNoLeido, 1)
                        .currentDate("actualizadoEn"),
                ConversacionDocument.class);
    }

    private String generarPreview(MensajeDocument mensaje) {
        if (mensaje.getTipo() != TipoMensaje.TEXTO) {
            return "[" + mensaje.getTipo() + "]";
        }
        String c = mensaje.getContenido() == null ? "" : mensaje.getContenido();
        return c.length() > 120 ? c.substring(0, 120) + "…" : c;
    }
}
