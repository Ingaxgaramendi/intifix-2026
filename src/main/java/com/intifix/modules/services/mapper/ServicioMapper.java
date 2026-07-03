package com.intifix.modules.services.mapper;

import com.intifix.modules.services.dto.request.ActualizarServicioRequest;
import com.intifix.modules.services.dto.request.CrearServicioRequest;
import com.intifix.modules.services.dto.response.ServicioResponse;
import com.intifix.modules.services.entity.Servicio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ServicioMapper {

    @Mapping(target = "idServicio", ignore = true)
    @Mapping(target = "idCliente", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "fechaFinalizacion", ignore = true)
    @Mapping(target = "motivoCancelacion", ignore = true)
    Servicio toEntity(CrearServicioRequest request);

    @Mapping(target = "nombreCliente", ignore = true)
    ServicioResponse toResponse(Servicio entity);

    @Mapping(target = "idServicio", ignore = true)
    @Mapping(target = "idCliente", ignore = true)
    @Mapping(target = "idUbicacion", ignore = true)
    @Mapping(target = "fotos", ignore = true)
    @Mapping(target = "tipoSolicitud", ignore = true)
    @Mapping(target = "idTecnicoDirecto", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "fechaFinalizacion", ignore = true)
    @Mapping(target = "motivoCancelacion", ignore = true)
    void updateEntityFromDto(ActualizarServicioRequest request, @MappingTarget Servicio entity);
}
