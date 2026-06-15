package com.intifix.modules.audit.service;

import com.intifix.modules.audit.dto.response.ApiLogResponse;
import com.intifix.modules.audit.entity.ApiLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApiLogService {

    void registrar(ApiLogDocument log);

    Page<ApiLogResponse> listar(Pageable pageable);
}
