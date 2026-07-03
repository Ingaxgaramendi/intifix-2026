package com.intifix.modules.services.mapper;

import com.intifix.modules.services.dto.request.CrearReporteRequest;
import com.intifix.modules.services.dto.response.ReporteResponse;
import com.intifix.modules.services.entity.Reporte;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReporteMapper {

    @Mapping(target = "idReporte", ignore = true)
    @Mapping(target = "idReportante", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "resolucion", ignore = true)
    @Mapping(target = "accionTomada", ignore = true)
    @Mapping(target = "resueltoPor", ignore = true)
    @Mapping(target = "fechaResolucion", ignore = true)
    @Mapping(target = "fechaReporte", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "metadatos", ignore = true)
    Reporte toEntity(CrearReporteRequest request);

    ReporteResponse toResponse(Reporte entity);
}
