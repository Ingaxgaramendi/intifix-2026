package com.intifix.modules.services.mapper;

import com.intifix.modules.services.dto.request.AsignarTecnicoRequest;
import com.intifix.modules.services.dto.response.AsignacionServicioResponse;
import com.intifix.modules.services.entity.AsignacionServicio;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for AsignacionServicio entity and DTOs.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AsignacionServicioMapper {

    AsignacionServicio toEntity(AsignarTecnicoRequest request);

    AsignacionServicioResponse toResponse(AsignacionServicio entity);

    void updateEntityFromDto(AsignarTecnicoRequest request, @MappingTarget AsignacionServicio entity);
}
