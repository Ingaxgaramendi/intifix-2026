package com.intifix.modules.audit.service;

import com.intifix.modules.audit.dto.response.GeoLogResponse;
import com.intifix.modules.audit.entity.GeoLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GeoLogService {

    void registrar(GeoLogDocument log);

    Page<GeoLogResponse> listar(Pageable pageable);
}
