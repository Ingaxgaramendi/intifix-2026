package com.intifix.modules.services.mapper;

import com.intifix.modules.services.dto.request.AsignarTecnicoRequest;
import com.intifix.modules.services.dto.response.AsignacionServicioResponse;
import com.intifix.modules.services.entity.AsignacionServicio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AsignacionServicioMapper {

    @Mapping(target = "idAsignacion", ignore = true)
    @Mapping(target = "idServicio", ignore = true)
    @Mapping(target = "fechaAsignacion", ignore = true)
    @Mapping(target = "fechaInicioReal", ignore = true)
    @Mapping(target = "fechaFinReal", ignore = true)
    @Mapping(target = "estadoServicio", ignore = true)
    @Mapping(target = "notasCliente", ignore = true)
    AsignacionServicio toEntity(AsignarTecnicoRequest request);

    @Mapping(target = "tituloServicio", ignore = true)
    @Mapping(target = "idCliente", ignore = true)
    @Mapping(target = "nombreCliente", ignore = true)
    AsignacionServicioResponse toResponse(AsignacionServicio entity);

    @Mapping(target = "idAsignacion", ignore = true)
    @Mapping(target = "idServicio", ignore = true)
    @Mapping(target = "fechaAsignacion", ignore = true)
    @Mapping(target = "fechaInicioReal", ignore = true)
    @Mapping(target = "fechaFinReal", ignore = true)
    @Mapping(target = "estadoServicio", ignore = true)
    @Mapping(target = "notasCliente", ignore = true)
    void updateEntityFromDto(AsignarTecnicoRequest request, @MappingTarget AsignacionServicio entity);
}
