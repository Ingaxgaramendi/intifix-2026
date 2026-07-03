package com.intifix.modules.technicians.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignarEspecialidadRequest {

    @NotNull(message = "El idUsuarioTecnico es obligatorio")
    private UUID idUsuarioTecnico;

    @NotNull(message = "El idEspecialidad es obligatorio")
    private UUID idEspecialidad;

    /** Certificado que acredita la especialidad (URL en Cloudinary). Opcional al asignar. */
    private String certificadoUrl;
}
