package com.intifix.modules.notifications.service;

import com.intifix.modules.notifications.dto.response.NotificacionResponse;
import com.intifix.modules.notifications.entity.TipoNotificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificacionService {

    /**
     * Crea y persiste una notificación para un destinatario. API interna usada
     * por los listeners de dominio (no toma el usuario del contexto).
     */
    NotificacionResponse notificar(UUID idDestinatario, TipoNotificacion tipo,
                                   String titulo, String cuerpo, UUID referenciaId);

    Page<NotificacionResponse> misNotificaciones(Pageable pageable);

    Page<NotificacionResponse> misNoLeidas(Pageable pageable);

    long contarNoLeidas();

    void marcarLeida(UUID idNotificacion);

    long marcarTodasLeidas();

    void eliminar(UUID idNotificacion);
}
