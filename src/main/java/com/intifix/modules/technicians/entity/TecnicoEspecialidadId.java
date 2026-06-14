package com.intifix.modules.technicians.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Clave primaria compuesta de {@link TecnicoEspecialidad}, espejo de
 * {@code PRIMARY KEY (id_usuario_tecnico, id_especialidad)} en la BD.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TecnicoEspecialidadId implements Serializable {

    private UUID idUsuarioTecnico;
    private UUID idEspecialidad;
}
