package com.intifix.modules.payments.dto.response;

import com.intifix.modules.payments.entity.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenPagoResponse {

    private Long totalPagos;
    private Long pagosPendientes;
    private Long pagosPagados;
    private Long pagosReembolsados;
    private Long pagosFallidos;
    private BigDecimal montoTotalProcesado;
    private BigDecimal montoTotalPendiente;
    private Map<EstadoPago, Long> conteoPorEstado;
}
