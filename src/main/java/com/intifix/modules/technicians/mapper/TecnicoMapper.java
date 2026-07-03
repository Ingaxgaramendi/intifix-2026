package com.intifix.modules.technicians.mapper;

import com.intifix.modules.technicians.dto.request.ActualizarTecnicoRequest;
import com.intifix.modules.technicians.dto.request.CrearTecnicoRequest;
import com.intifix.modules.technicians.dto.response.TecnicoDetalleResponse;
import com.intifix.modules.technicians.dto.response.TecnicoResponse;
import com.intifix.modules.technicians.entity.PerfilTecnico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TecnicoMapper {

    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "fotoPerfilUrl", ignore = true)
    @Mapping(target = "descripcion", ignore = true)
    @Mapping(target = "telefonoContacto", ignore = true)
    PerfilTecnico toEntity(CrearTecnicoRequest request);

    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    void updateEntityFromDto(ActualizarTecnicoRequest request, @MappingTarget PerfilTecnico entity);

    @Mapping(target = "estadoUsuario", ignore = true)
    TecnicoResponse toResponse(PerfilTecnico entity);

    @Mapping(target = "horarios", ignore = true)
    @Mapping(target = "especialidades", ignore = true)
    @Mapping(target = "reputacion", ignore = true)
    @Mapping(target = "estadoUsuario", ignore = true)
    TecnicoDetalleResponse toDetalleResponse(PerfilTecnico entity);

    List<TecnicoResponse> toResponseList(List<PerfilTecnico> entities);
}
