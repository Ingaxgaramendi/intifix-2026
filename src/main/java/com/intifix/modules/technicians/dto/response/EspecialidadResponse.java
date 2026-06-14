package com.intifix.modules.technicians.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspecialidadResponse {

    private UUID idEspecialidad;
    private String nombre;
    private String descripcion;
}
