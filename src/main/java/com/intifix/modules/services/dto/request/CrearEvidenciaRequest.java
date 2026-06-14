package com.intifix.modules.services.dto.request;

import com.intifix.modules.services.enums.TipoArchivo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating evidence for a service.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearEvidenciaRequest {

    @NotNull(message = "El idServicio es obligatorio")
    private UUID idServicio;

    @NotBlank(message = "La URL del archivo es obligatoria")
    @Size(max = 1000, message = "La URL del archivo no puede exceder 1000 caracteres")
    @Pattern(regexp = "^(https?://|ftp://)?[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$", message = "La URL del archivo no es válida")
    private String urlArchivo;

    @NotBlank(message = "El nombre del archivo es obligatorio")
    @Size(max = 255, message = "El nombre del archivo no puede exceder 255 caracteres")
    private String nombreArchivo;

    @NotNull(message = "El tipo de archivo es obligatorio")
    private TipoArchivo tipoArchivo;

    @Positive(message = "El tamaño en bytes debe ser positivo")
    private Long tamanoBytes;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "El usuario que sube es obligatorio")
    private UUID subidoPor;
}
