package com.intifix.modules.technicians.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Actualiza el certificado de una especialidad ya asignada a un técnico.
 * Sin patrón estricto de URL para admitir también URLs locales del fallback.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarCertificadoEspecialidadRequest {

    @NotBlank(message = "El certificado es obligatorio")
    @Size(max = 1000, message = "La URL del certificado no puede exceder 1000 caracteres")
    private String certificadoUrl;
}
