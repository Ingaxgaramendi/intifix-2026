package com.intifix.modules.payments.dto.response;

import com.intifix.modules.payments.entity.EstadoFiscalComprobante;
import com.intifix.modules.payments.entity.TipoComprobante;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponse {

    private UUID idFactura;
    private UUID idPago;
    private String codigoComprobante;
    private TipoComprobante tipo;
    private EstadoFiscalComprobante estadoFiscal;
    private String urlPdf;
    private UUID idFacturaReferencia;
    private ZonedDateTime fechaEmision;
}
