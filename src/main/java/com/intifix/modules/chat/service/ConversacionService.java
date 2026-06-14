package com.intifix.modules.chat.service;

import com.intifix.modules.chat.dto.request.CrearConversacionRequest;
import com.intifix.modules.chat.dto.response.ConversacionResponse;
import com.intifix.modules.chat.entity.ConversacionDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ConversacionService {

    ConversacionResponse crear(CrearConversacionRequest request);

    Page<ConversacionResponse> misConversaciones(Pageable pageable);

    ConversacionResponse obtenerPorId(UUID idConversacion);

    void archivar(UUID idConversacion);

    void bloquear(UUID idConversacion);

    void eliminar(UUID idConversacion);

    /**
     * Carga la conversación verificando que {@code idUsuario} sea participante
     * (anti-IDOR). Uso interno del módulo (p.ej. el servicio de mensajes).
     */
    ConversacionDocument cargarParticipando(UUID idConversacion, UUID idUsuario);
}
