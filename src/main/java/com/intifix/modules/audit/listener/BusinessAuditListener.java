package com.intifix.modules.audit.listener;

import com.intifix.modules.audit.config.AuditRequestContext;
import com.intifix.modules.audit.entity.AuditAction;
import com.intifix.modules.audit.entity.AuditEventDocument;
import com.intifix.modules.audit.entity.AuditModule;
import com.intifix.modules.audit.event.InvoiceGeneratedEvent;
import com.intifix.modules.audit.event.PaymentCompletedEvent;
import com.intifix.modules.audit.event.ServiceCancelledEvent;
import com.intifix.modules.audit.event.ServiceCreatedEvent;
import com.intifix.modules.audit.event.TechnicianApprovedEvent;
import com.intifix.modules.audit.event.UserCreatedEvent;
import com.intifix.modules.audit.event.UserUpdatedEvent;
import com.intifix.modules.audit.service.AuditEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Consume los eventos de negocio de los módulos productores y los persiste en
 * {@code audit_events}. Los productores publican eventos desacoplados (Spring
 * Events) sin conocer a audit.
 *
 * <p>Los handlers son síncronos a propósito: corren en el hilo de la request, de
 * modo que {@link AuditRequestContext} puede leer IP/User-Agent/usuario. La
 * escritura en Mongo sí es asíncrona (ver {@code AuditEventServiceImpl}).</p>
 */
@Component
@RequiredArgsConstructor
public class BusinessAuditListener {

    private final AuditEventService auditEventService;

    @EventListener
    public void handle(UserCreatedEvent event) {
        registrar(AuditModule.USERS, AuditAction.CREAR, event.getClass().getSimpleName(),
                new AuditResource(event.userId(), "Usuario"), event.userId(), null, event);
    }

    @EventListener
    public void handle(UserUpdatedEvent event) {
        registrar(AuditModule.USERS, AuditAction.ACTUALIZAR, event.getClass().getSimpleName(),
                new AuditResource(event.userId(), "Usuario"), AuditRequestContext.currentUserIdOrNull(),
                event.oldValue(), event.newValue());
    }

    @EventListener
    public void handle(TechnicianApprovedEvent event) {
        registrar(AuditModule.TECHNICIANS, AuditAction.APROBAR, event.getClass().getSimpleName(),
                new AuditResource(event.technicianId(), "Tecnico"), event.approvedBy(), null, null);
    }

    @EventListener
    public void handle(ServiceCreatedEvent event) {
        registrar(AuditModule.SERVICES, AuditAction.CREAR, event.getClass().getSimpleName(),
                new AuditResource(event.serviceId(), "Servicio"), event.clientId(), null, null);
    }

    @EventListener
    public void handle(ServiceCancelledEvent event) {
        registrar(AuditModule.SERVICES, AuditAction.CANCELAR, event.getClass().getSimpleName(),
                new AuditResource(event.serviceId(), "Servicio"), event.cancelledBy(), null, event.motivo());
    }

    @EventListener
    public void handle(PaymentCompletedEvent event) {
        registrar(AuditModule.PAYMENTS, AuditAction.PAGAR, event.getClass().getSimpleName(),
                new AuditResource(event.paymentId(), "Pago"), event.userId(), null, event.monto());
    }

    @EventListener
    public void handle(InvoiceGeneratedEvent event) {
        registrar(AuditModule.PAYMENTS, AuditAction.EMITIR, event.getClass().getSimpleName(),
                new AuditResource(event.invoiceId(), "Factura"), event.userId(), null, null);
    }

    private record AuditResource(UUID id, String type) {}

    private void registrar(AuditModule module, AuditAction action, String eventType,
                           AuditResource resource, UUID actorUserId,
                           Object oldValue, Object newValue) {
        AuditEventDocument evento = AuditEventDocument.builder()
                .eventId(UUID.randomUUID())
                .eventType(eventType)
                .module(module)
                .action(action)
                .resourceId(resource.id())
                .resourceType(resource.type())
                .userId(actorUserId)
                .oldValue(oldValue)
                .newValue(newValue)
                .ipAddress(AuditRequestContext.clientIp())
                .userAgent(AuditRequestContext.userAgent())
                .build();
        auditEventService.registrar(evento);
    }
}
