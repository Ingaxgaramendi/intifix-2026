package com.intifix.modules.audit.service.impl;

import com.intifix.modules.audit.dto.response.ApiLogResponse;
import com.intifix.modules.audit.entity.ApiLogDocument;
import com.intifix.modules.audit.mapper.AuditMapper;
import com.intifix.modules.audit.repository.ApiLogRepository;
import com.intifix.modules.audit.service.ApiLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiLogServiceImpl implements ApiLogService {

    private final ApiLogRepository repository;
    private final AuditMapper mapper;

    @Override
    @Async("auditExecutor")
    public void registrar(ApiLogDocument log) {
        try {
            repository.save(log);
        } catch (Exception e) {
            ApiLogServiceImpl.log.error("No se pudo registrar el log HTTP {} {}: {}",
                    log.getMethod(), log.getPath(), e.getMessage());
        }
    }

    @Override
    public Page<ApiLogResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }
}
