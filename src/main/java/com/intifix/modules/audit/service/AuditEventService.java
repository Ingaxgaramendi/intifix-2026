package com.intifix.modules.audit.service;

import com.intifix.modules.audit.dto.request.AuditEventFilterRequest;
import com.intifix.modules.audit.dto.response.AuditEventResponse;
import com.intifix.modules.audit.entity.AuditEventDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Auditoría de eventos de negocio (audit_events). La escritura es asíncrona y
 * la consulta está restringida a administradores (gate en el controller).
 */
public interface AuditEventService {

    /** Persiste un evento de negocio. Invocado por listeners/aspectos, fuera del hilo de request. */
    void registrar(AuditEventDocument evento);

    /** Listado paginado para el panel de administración, con filtros opcionales. */
    Page<AuditEventResponse> listar(AuditEventFilterRequest filtro, Pageable pageable);
}
