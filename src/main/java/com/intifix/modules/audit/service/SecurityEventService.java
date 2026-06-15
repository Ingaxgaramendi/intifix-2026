package com.intifix.modules.audit.service;

import com.intifix.modules.audit.dto.request.SecurityEventFilterRequest;
import com.intifix.modules.audit.dto.response.SecurityEventResponse;
import com.intifix.modules.audit.entity.SecurityEventDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SecurityEventService {

    void registrar(SecurityEventDocument evento);

    Page<SecurityEventResponse> listar(SecurityEventFilterRequest filtro, Pageable pageable);
}
