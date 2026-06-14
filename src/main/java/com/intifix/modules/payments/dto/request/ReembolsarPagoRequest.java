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
public class ReembolsarPagoRequest {

    @NotNull(message = "El ID del pago es obligatorio")
    private UUID idPago;

    @NotBlank(message = "La razón del reembolso es obligatoria")
    @Size(min = 10, max = 500, message = "La razón del reembolso debe tener entre 10 y 500 caracteres")
    private String razon;
}
