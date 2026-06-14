package com.intifix.modules.payments.dto.response;

import com.intifix.modules.payments.entity.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoDetalleResponse {

    private UUID idPago;
    private UUID idServicio;
    private UUID idMetodoPago;
    private String nombreMetodoPago;
    private BigDecimal montoTotal;
    private BigDecimal comisionPlataforma;
    private BigDecimal montoNetoTecnico;
    private BigDecimal impuestoTotal;
    private EstadoPago estado;
    private String transactionId;
    private ZonedDateTime fechaPago;
    private ZonedDateTime creadoEn;
    private FacturaResponse factura;
}
