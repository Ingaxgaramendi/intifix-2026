package com.intifix.modules.payments.mapper;

import com.intifix.modules.payments.dto.request.CrearMetodoPagoRequest;
import com.intifix.modules.payments.dto.response.MetodoPagoResponse;
import com.intifix.modules.payments.entity.MetodoPago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MetodoPagoMapper {

    @Mapping(target = "idMetodoPago", ignore = true)
    MetodoPago toEntity(CrearMetodoPagoRequest request);

    MetodoPagoResponse toResponse(MetodoPago metodoPago);

    @Mapping(target = "idMetodoPago", ignore = true)
    void updateEntityFromRequest(CrearMetodoPagoRequest request, @MappingTarget MetodoPago metodoPago);
}
