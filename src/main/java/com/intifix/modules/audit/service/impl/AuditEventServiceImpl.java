package com.intifix.modules.audit.service.impl;

import com.intifix.modules.audit.dto.request.AuditEventFilterRequest;
import com.intifix.modules.audit.dto.response.AuditEventResponse;
import com.intifix.modules.audit.entity.AuditEventDocument;
import com.intifix.modules.audit.mapper.AuditMapper;
import com.intifix.modules.audit.repository.AuditEventRepository;
import com.intifix.modules.audit.service.AuditEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Escritura asíncrona (no bloquea el hilo de negocio/request) y lectura
 * paginada. Una falla al auditar nunca debe tumbar la operación de negocio:
 * los errores se loguean y se descartan.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventServiceImpl implements AuditEventService {

    private final AuditEventRepository repository;
    private final AuditMapper mapper;

    @Override
    @Async("auditExecutor")
    public void registrar(AuditEventDocument evento) {
        try {
            repository.save(evento);
        } catch (Exception e) {
            log.error("No se pudo registrar el evento de auditoría {}: {}", evento.getEventType(), e.getMessage());
        }
    }

    @Override
    public Page<AuditEventResponse> listar(AuditEventFilterRequest filtro, Pageable pageable) {
        Page<AuditEventDocument> page;
        if (filtro == null) {
            page = repository.findAll(pageable);
        } else if (filtro.module() != null && filtro.userId() != null) {
            page = repository.findByModuleAndUserId(filtro.module(), filtro.userId(), pageable);
        } else if (filtro.module() != null) {
            page = repository.findByModule(filtro.module(), pageable);
        } else if (filtro.userId() != null) {
            page = repository.findByUserId(filtro.userId(), pageable);
        } else if (filtro.eventType() != null) {
            page = repository.findByEventType(filtro.eventType(), pageable);
        } else {
            page = repository.findAll(pageable);
        }
        return page.map(mapper::toResponse);
    }
}
