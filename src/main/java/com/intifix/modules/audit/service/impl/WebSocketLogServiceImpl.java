package com.intifix.modules.audit.service.impl;

import com.intifix.modules.audit.dto.response.WebSocketLogResponse;
import com.intifix.modules.audit.entity.WebSocketLogDocument;
import com.intifix.modules.audit.mapper.AuditMapper;
import com.intifix.modules.audit.repository.WebSocketLogRepository;
import com.intifix.modules.audit.service.WebSocketLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketLogServiceImpl implements WebSocketLogService {

    private final WebSocketLogRepository repository;
    private final AuditMapper mapper;

    @Override
    @Async("auditExecutor")
    public void registrar(WebSocketLogDocument logDoc) {
        try {
            repository.save(logDoc);
        } catch (Exception e) {
            log.error("No se pudo registrar el log WebSocket {}: {}", logDoc.getAction(), e.getMessage());
        }
    }

    @Override
    public Page<WebSocketLogResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }
}
