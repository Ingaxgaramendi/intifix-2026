package com.intifix.modules.payments.dto.request;

import com.intifix.modules.payments.entity.TipoComprobante;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearFacturaRequest {

    @NotNull(message = "El ID del pago es obligatorio")
    private UUID idPago;

    @NotNull(message = "El tipo de comprobante es obligatorio")
    private TipoComprobante tipo;

    @Size(max = 100, message = "El código de comprobante no puede exceder 100 caracteres")
    private String codigoComprobante;

    private UUID idFacturaReferencia;
}
