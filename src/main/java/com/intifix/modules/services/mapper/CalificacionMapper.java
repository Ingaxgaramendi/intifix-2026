package com.intifix.modules.services.mapper;

import com.intifix.modules.services.dto.request.CrearCalificacionRequest;
import com.intifix.modules.services.dto.response.CalificacionResponse;
import com.intifix.modules.services.entity.Calificacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalificacionMapper {

    @Mapping(target = "idCalificacion", ignore = true)
    @Mapping(target = "idUsuarioTecnico", ignore = true)
    @Mapping(target = "idCliente", ignore = true)
    @Mapping(target = "fechaCalificacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Calificacion toEntity(CrearCalificacionRequest request);

    CalificacionResponse toResponse(Calificacion entity);
}
