package com.intifix.modules.technicians.exception;

import java.util.UUID;

public class EspecialidadNoEncontradaException extends TecnicoException {

    public EspecialidadNoEncontradaException(String message) {
        super(message, "ESPECIALIDAD_NOT_FOUND");
    }

    public EspecialidadNoEncontradaException(String message, Throwable cause) {
        super(message, "ESPECIALIDAD_NOT_FOUND", cause);
    }

    public static EspecialidadNoEncontradaException byIdEspecialidad(UUID idEspecialidad) {
        return new EspecialidadNoEncontradaException("Especialidad no encontrada con idEspecialidad: " + idEspecialidad);
    }

    public static EspecialidadNoEncontradaException byNombre(String nombre) {
        return new EspecialidadNoEncontradaException("Especialidad no encontrada con nombre: " + nombre);
    }
}
