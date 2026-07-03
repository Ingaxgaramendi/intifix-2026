package com.intifix.modules.services.mapper;

import com.intifix.modules.services.dto.request.CrearCotizacionRequest;
import com.intifix.modules.services.dto.response.CotizacionResponse;
import com.intifix.modules.services.entity.Cotizacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CotizacionMapper {

    @Mapping(target = "idCotizacion", ignore = true)
    @Mapping(target = "idUsuarioTecnico", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaEnvio", ignore = true)
    @Mapping(target = "fechaRespuesta", ignore = true)
    @Mapping(target = "fechaExpiracion", ignore = true)
    @Mapping(target = "motivoRechazo", ignore = true)
    Cotizacion toEntity(CrearCotizacionRequest request);

    CotizacionResponse toResponse(Cotizacion entity);
}
