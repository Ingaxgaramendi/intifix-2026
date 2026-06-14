package com.intifix.modules.payments.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class ConfirmarPagoRequest {

    @NotNull(message = "El ID del pago es obligatorio")
    private UUID idPago;

    @NotBlank(message = "El ID de transacción es obligatorio")
    @Size(max = 255, message = "El ID de transacción no puede exceder 255 caracteres")
    private String transactionId;
}
