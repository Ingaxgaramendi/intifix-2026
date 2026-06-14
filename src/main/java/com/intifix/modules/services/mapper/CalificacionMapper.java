package com.intifix.modules.services.mapper;

import com.intifix.modules.services.dto.request.CrearCalificacionRequest;
import com.intifix.modules.services.dto.response.CalificacionResponse;
import com.intifix.modules.services.entity.Calificacion;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for Calificacion entity and DTOs.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface CalificacionMapper {

    Calificacion toEntity(CrearCalificacionRequest request);

    CalificacionResponse toResponse(Calificacion entity);
}
