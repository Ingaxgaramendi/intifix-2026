package com.intifix.modules.chat.service;

import com.intifix.modules.chat.dto.request.EditarMensajeRequest;
import com.intifix.modules.chat.dto.request.EnviarMensajeRequest;
import com.intifix.modules.chat.dto.response.MensajeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MensajeService {

    MensajeResponse enviar(EnviarMensajeRequest request);

    MensajeResponse editar(UUID idMensaje, EditarMensajeRequest request);

    void eliminar(UUID idMensaje);

    Page<MensajeResponse> historial(UUID idConversacion, Pageable pageable);

    /**
     * Marca como leídos los mensajes recibidos por el usuario actual en la
     * conversación y resetea su contador de no leídos. Devuelve cuántos marcó.
     */
    long marcarLeida(UUID idConversacion);

    long contarNoLeidos(UUID idConversacion);
}
