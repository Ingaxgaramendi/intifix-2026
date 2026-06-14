package com.intifix.modules.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metadatos de un archivo ya subido a almacenamiento externo
 * (Cloudinary/S3/MinIO). El backend nunca recibe el binario, solo la URL.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjuntoRequest {

    @NotBlank(message = "La URL del adjunto es obligatoria")
    @Pattern(regexp = "^https?://.+", message = "La URL del adjunto debe ser http(s)")
    @Size(max = 2048, message = "La URL no puede exceder 2048 caracteres")
    private String url;

    @NotBlank(message = "El nombre del archivo es obligatorio")
    @Size(max = 255, message = "El nombre del archivo no puede exceder 255 caracteres")
    private String nombreArchivo;

    @NotBlank(message = "El tipo MIME es obligatorio")
    @Size(max = 100, message = "El tipo MIME no puede exceder 100 caracteres")
    private String tipoMime;

    @Positive(message = "El tamaño del archivo debe ser mayor a 0")
    private Long tamanoBytes;
}
