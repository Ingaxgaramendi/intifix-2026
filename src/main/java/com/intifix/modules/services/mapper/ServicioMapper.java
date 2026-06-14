package com.intifix.modules.services.mapper;

import com.intifix.modules.services.dto.request.ActualizarServicioRequest;
import com.intifix.modules.services.dto.request.CrearServicioRequest;
import com.intifix.modules.services.dto.response.ServicioResponse;
import com.intifix.modules.services.entity.Servicio;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for Servicio entity and DTOs.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ServicioMapper {

    Servicio toEntity(CrearServicioRequest request);

    ServicioResponse toResponse(Servicio entity);

    void updateEntityFromDto(ActualizarServicioRequest request, @MappingTarget Servicio entity);
}
