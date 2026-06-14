package com.intifix.modules.services.mapper;

import com.intifix.modules.services.dto.request.CrearReporteRequest;
import com.intifix.modules.services.dto.response.ReporteResponse;
import com.intifix.modules.services.entity.Reporte;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for Reporte entity and DTOs.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface ReporteMapper {

    Reporte toEntity(CrearReporteRequest request);

    ReporteResponse toResponse(Reporte entity);
}
