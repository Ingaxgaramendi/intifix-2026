package com.intifix.modules.technicians.mapper;

import com.intifix.modules.technicians.dto.request.CrearExcepcionHorarioRequest;
import com.intifix.modules.technicians.dto.response.ExcepcionHorarioResponse;
import com.intifix.modules.technicians.entity.ExcepcionHorarioTecnico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ExcepcionHorarioMapper {

    @Mapping(target = "idExcepcion", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    ExcepcionHorarioTecnico toEntity(CrearExcepcionHorarioRequest request);

    ExcepcionHorarioResponse toResponse(ExcepcionHorarioTecnico entity);

    List<ExcepcionHorarioResponse> toResponseList(List<ExcepcionHorarioTecnico> entities);
}
