package com.intifix.modules.notifications.listener;

import com.intifix.modules.audit.event.CertificadoAprobadoEvent;
import com.intifix.modules.audit.event.CertificadoRechazadoEvent;
import com.intifix.modules.audit.event.TechnicianApprovedEvent;
import com.intifix.modules.audit.event.TechnicianRejectedEvent;
import com.intifix.modules.notifications.dto.response.NotificacionResponse;
import com.intifix.modules.notifications.entity.TipoNotificacion;
import com.intifix.modules.notifications.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TecnicoNotificationListener {

    private final NotificacionService notificacionService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void alAprobarTecnico(TechnicianApprovedEvent evento) {
        NotificacionResponse notif = notificacionService.notificar(
                evento.technicianId(),
                TipoNotificacion.SISTEMA,
                "¡Perfil aprobado!",
                "Tu perfil de técnico ha sido aprobado. Ya puedes cotizar servicios.",
                evento.technicianId());
        push(evento.technicianId().toString(), notif);
        log.debug("Notificación de aprobación de perfil enviada al técnico {}", evento.technicianId());
    }

    @EventListener
    public void alRechazarTecnico(TechnicianRejectedEvent evento) {
        NotificacionResponse notif = notificacionService.notificar(
                evento.technicianId(),
                TipoNotificacion.SISTEMA,
                "Perfil no aprobado",
                "Tu perfil de técnico no fue aprobado. Revisa tus documentos y vuelve a intentarlo.",
                evento.technicianId());
        push(evento.technicianId().toString(), notif);
        log.debug("Notificación de rechazo de perfil enviada al técnico {}", evento.technicianId());
    }

    @EventListener
    public void alAprobarCertificado(CertificadoAprobadoEvent evento) {
        NotificacionResponse notif = notificacionService.notificar(
                evento.idTecnico(),
                TipoNotificacion.SISTEMA,
                "Certificado aprobado",
                "Tu certificado de " + evento.nombreEspecialidad() + " fue aprobado. Ya puedes cotizar servicios de esta especialidad.",
                evento.idEspecialidad());
        push(evento.idTecnico().toString(), notif);
        log.debug("Notificación de aprobación de certificado ({}) enviada al técnico {}",
                evento.nombreEspecialidad(), evento.idTecnico());
    }

    @EventListener
    public void alRechazarCertificado(CertificadoRechazadoEvent evento) {
        NotificacionResponse notif = notificacionService.notificar(
                evento.idTecnico(),
                TipoNotificacion.SISTEMA,
                "Certificado rechazado",
                "Tu certificado de " + evento.nombreEspecialidad() + " fue rechazado. Sube un nuevo certificado para que sea revisado.",
                evento.idEspecialidad());
        push(evento.idTecnico().toString(), notif);
        log.debug("Notificación de rechazo de certificado ({}) enviada al técnico {}",
                evento.nombreEspecialidad(), evento.idTecnico());
    }

    private void push(String idDestinatario, NotificacionResponse notif) {
        messagingTemplate.convertAndSendToUser(idDestinatario, "/queue/notifications", notif);
    }
}
