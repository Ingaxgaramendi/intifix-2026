package com.intifix.modules.technicians.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Especialidad asignada a un técnico, incluyendo el certificado que la acredita.
 * Se usa en el listado "mis especialidades" del perfil del técnico.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspecialidadTecnicoResponse {

    private UUID idEspecialidad;
    private String nombre;
    private String descripcion;
    private String certificadoUrl;
}
