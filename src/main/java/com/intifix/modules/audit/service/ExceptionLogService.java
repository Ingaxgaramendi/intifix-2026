package com.intifix.modules.audit.service;

import com.intifix.modules.audit.dto.response.ExceptionLogResponse;
import com.intifix.modules.audit.entity.ExceptionLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExceptionLogService {

    void registrar(ExceptionLogDocument log);

    Page<ExceptionLogResponse> listar(Pageable pageable);
}
