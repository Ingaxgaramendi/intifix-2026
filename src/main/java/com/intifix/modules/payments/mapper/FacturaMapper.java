package com.intifix.modules.payments.mapper;

import com.intifix.modules.payments.dto.request.CrearFacturaRequest;
import com.intifix.modules.payments.dto.response.FacturaResponse;
import com.intifix.modules.payments.entity.Factura;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FacturaMapper {

    @Mapping(target = "idFactura", ignore = true)
    @Mapping(target = "tipo", source = "tipo")
    @Mapping(target = "estadoFiscal", constant = "PENDIENTE")
    @Mapping(target = "urlPdf", ignore = true)
    @Mapping(target = "fechaEmision", ignore = true)
    Factura toEntity(CrearFacturaRequest request);

    FacturaResponse toResponse(Factura factura);

    @Mapping(target = "idFactura", ignore = true)
    @Mapping(target = "idPago", ignore = true)
    @Mapping(target = "fechaEmision", ignore = true)
    @Mapping(target = "estadoFiscal", ignore = true)
    @Mapping(target = "urlPdf", ignore = true)
    void updateEntityFromRequest(CrearFacturaRequest request, @MappingTarget Factura factura);
}
