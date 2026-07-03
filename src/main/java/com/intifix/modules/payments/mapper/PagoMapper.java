package com.intifix.modules.payments.mapper;

import com.intifix.modules.payments.dto.request.CrearPagoRequest;
import com.intifix.modules.payments.dto.response.PagoResponse;
import com.intifix.modules.payments.entity.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    @Mapping(target = "idPago", ignore = true)
    @Mapping(target = "estado", constant = "PENDIENTE")
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "fechaPago", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    Pago toEntity(CrearPagoRequest request);

    PagoResponse toResponse(Pago pago);

    @Mapping(target = "idPago", ignore = true)
    @Mapping(target = "idServicio", ignore = true)
    @Mapping(target = "idMetodoPago", ignore = true)
    @Mapping(target = "montoTotal", ignore = true)
    @Mapping(target = "comisionPlataforma", ignore = true)
    @Mapping(target = "montoNetoTecnico", ignore = true)
    @Mapping(target = "impuestoTotal", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "fechaPago", ignore = true)
    void updateEntityFromRequest(CrearPagoRequest request, @MappingTarget Pago pago);

    @Named("validarSumaMontos")
    default boolean validarSumaMontos(BigDecimal montoTotal, BigDecimal comisionPlataforma,
                                      BigDecimal montoNetoTecnico, BigDecimal impuestoTotal) {
        if (montoTotal == null || comisionPlataforma == null ||
            montoNetoTecnico == null || impuestoTotal == null) {
            return false;
        }
        BigDecimal suma = comisionPlataforma.add(montoNetoTecnico).add(impuestoTotal);
        return suma.compareTo(montoTotal) == 0;
    }
}
