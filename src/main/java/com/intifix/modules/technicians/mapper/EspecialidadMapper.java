package com.intifix.modules.technicians.mapper;

import com.intifix.modules.technicians.dto.request.ActualizarEspecialidadRequest;
import com.intifix.modules.technicians.dto.request.CrearEspecialidadRequest;
import com.intifix.modules.technicians.dto.response.EspecialidadResponse;
import com.intifix.modules.technicians.entity.Especialidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EspecialidadMapper {

    @Mapping(target = "idEspecialidad", ignore = true)
    Especialidad toEntity(CrearEspecialidadRequest request);

    @Mapping(target = "idEspecialidad", ignore = true)
    void updateEntityFromDto(ActualizarEspecialidadRequest request, @MappingTarget Especialidad entity);

    EspecialidadResponse toResponse(Especialidad entity);

    List<EspecialidadResponse> toResponseList(List<Especialidad> entities);
}
