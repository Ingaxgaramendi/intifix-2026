package com.intifix.modules.technicians.mapper;

import com.intifix.modules.technicians.dto.request.ActualizarHorarioRequest;
import com.intifix.modules.technicians.dto.request.CrearHorarioRequest;
import com.intifix.modules.technicians.dto.response.HorarioResponse;
import com.intifix.modules.technicians.entity.HorarioTecnico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface HorarioMapper {

    @Mapping(target = "idHorario", ignore = true)
    HorarioTecnico toEntity(CrearHorarioRequest request);

    @Mapping(target = "idHorario", ignore = true)
    @Mapping(target = "idUsuarioTecnico", ignore = true)
    void updateEntityFromDto(ActualizarHorarioRequest request, @MappingTarget HorarioTecnico entity);

    HorarioResponse toResponse(HorarioTecnico entity);

    List<HorarioResponse> toResponseList(List<HorarioTecnico> entities);
}
