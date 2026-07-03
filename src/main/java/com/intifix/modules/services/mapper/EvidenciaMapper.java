package com.intifix.modules.services.mapper;

import com.intifix.modules.services.dto.request.CrearEvidenciaRequest;
import com.intifix.modules.services.dto.response.EvidenciaServicioResponse;
import com.intifix.modules.services.entity.EvidenciaServicio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EvidenciaMapper {

    @Mapping(target = "idEvidencia", ignore = true)
    @Mapping(target = "fechaSubida", ignore = true)
    @Mapping(target = "metadatos", ignore = true)
    EvidenciaServicio toEntity(CrearEvidenciaRequest request);

    EvidenciaServicioResponse toResponse(EvidenciaServicio entity);
}
