package com.intifix.modules.services.mapper;

import com.intifix.modules.services.dto.request.CrearEvidenciaRequest;
import com.intifix.modules.services.dto.response.EvidenciaServicioResponse;
import com.intifix.modules.services.entity.EvidenciaServicio;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for EvidenciaServicio entity and DTOs.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface EvidenciaMapper {

    EvidenciaServicio toEntity(CrearEvidenciaRequest request);

    EvidenciaServicioResponse toResponse(EvidenciaServicio entity);
}
