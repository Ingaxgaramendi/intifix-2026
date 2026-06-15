package com.intifix.modules.audit.mapper;

import com.intifix.modules.audit.dto.response.ApiLogResponse;
import com.intifix.modules.audit.dto.response.AuditEventResponse;
import com.intifix.modules.audit.dto.response.ExceptionLogResponse;
import com.intifix.modules.audit.dto.response.GeoLogResponse;
import com.intifix.modules.audit.dto.response.SecurityEventResponse;
import com.intifix.modules.audit.dto.response.WebSocketLogResponse;
import com.intifix.modules.audit.entity.ApiLogDocument;
import com.intifix.modules.audit.entity.AuditEventDocument;
import com.intifix.modules.audit.entity.ExceptionLogDocument;
import com.intifix.modules.audit.entity.GeoLogDocument;
import com.intifix.modules.audit.entity.SecurityEventDocument;
import com.intifix.modules.audit.entity.WebSocketLogDocument;
import org.mapstruct.Mapper;

/**
 * Conversión documento → DTO de lectura para los seis flujos de auditoría.
 * Las colecciones de auditoría son append-only: no hay mapeo inverso.
 */
@Mapper(componentModel = "spring")
public interface AuditMapper {

    AuditEventResponse toResponse(AuditEventDocument document);

    SecurityEventResponse toResponse(SecurityEventDocument document);

    ApiLogResponse toResponse(ApiLogDocument document);

    ExceptionLogResponse toResponse(ExceptionLogDocument document);

    WebSocketLogResponse toResponse(WebSocketLogDocument document);

    GeoLogResponse toResponse(GeoLogDocument document);
}
