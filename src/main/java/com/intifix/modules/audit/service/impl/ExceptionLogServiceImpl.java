package com.intifix.modules.audit.service.impl;

import com.intifix.modules.audit.dto.response.ExceptionLogResponse;
import com.intifix.modules.audit.entity.ExceptionLogDocument;
import com.intifix.modules.audit.mapper.AuditMapper;
import com.intifix.modules.audit.repository.ExceptionLogRepository;
import com.intifix.modules.audit.service.ExceptionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExceptionLogServiceImpl implements ExceptionLogService {

    private final ExceptionLogRepository repository;
    private final AuditMapper mapper;

    @Override
    @Async("auditExecutor")
    public void registrar(ExceptionLogDocument logDoc) {
        try {
            repository.save(logDoc);
        } catch (Exception e) {
            log.error("No se pudo registrar el log de excepción {}: {}",
                    logDoc.getExceptionClass(), e.getMessage());
        }
    }

    @Override
    public Page<ExceptionLogResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }
}
