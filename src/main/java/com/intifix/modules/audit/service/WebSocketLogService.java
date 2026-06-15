package com.intifix.modules.audit.service;

import com.intifix.modules.audit.dto.response.WebSocketLogResponse;
import com.intifix.modules.audit.entity.WebSocketLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WebSocketLogService {

    void registrar(WebSocketLogDocument log);

    Page<WebSocketLogResponse> listar(Pageable pageable);
}
