package com.intifix.modules.audit.service.impl;

import com.intifix.modules.audit.dto.request.SecurityEventFilterRequest;
import com.intifix.modules.audit.dto.response.SecurityEventResponse;
import com.intifix.modules.audit.entity.SecurityEventDocument;
import com.intifix.modules.audit.mapper.AuditMapper;
import com.intifix.modules.audit.repository.SecurityEventRepository;
import com.intifix.modules.audit.service.SecurityEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityEventServiceImpl implements SecurityEventService {

    private final SecurityEventRepository repository;
    private final AuditMapper mapper;

    @Override
    @Async("auditExecutor")
    public void registrar(SecurityEventDocument evento) {
        try {
            repository.save(evento);
        } catch (Exception e) {
            log.error("No se pudo registrar el evento de seguridad {}: {}", evento.getReason(), e.getMessage());
        }
    }

    @Override
    public Page<SecurityEventResponse> listar(SecurityEventFilterRequest filtro, Pageable pageable) {
        Page<SecurityEventDocument> page;
        if (filtro == null) {
            page = repository.findAll(pageable);
        } else if (filtro.reason() != null) {
            page = repository.findByReason(filtro.reason(), pageable);
        } else if (filtro.email() != null) {
            page = repository.findByEmail(filtro.email(), pageable);
        } else if (filtro.userId() != null) {
            page = repository.findByUserId(filtro.userId(), pageable);
        } else {
            page = repository.findAll(pageable);
        }
        return page.map(mapper::toResponse);
    }
}
