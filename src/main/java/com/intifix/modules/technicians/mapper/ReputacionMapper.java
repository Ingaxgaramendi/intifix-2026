package com.intifix.modules.technicians.mapper;

import com.intifix.modules.technicians.dto.response.ReputacionResponse;
import com.intifix.modules.technicians.entity.ReputacionTecnico;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
    componentModel = "spring"
)
public interface ReputacionMapper {

    ReputacionResponse toResponse(ReputacionTecnico entity);

    List<ReputacionResponse> toResponseList(List<ReputacionTecnico> entities);
}
