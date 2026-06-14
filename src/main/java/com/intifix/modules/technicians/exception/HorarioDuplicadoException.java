package com.intifix.modules.technicians.exception;

public class HorarioDuplicadoException extends TecnicoException {

    public HorarioDuplicadoException(String message) {
        super(message, "HORARIO_DUPLICADO");
    }

    public HorarioDuplicadoException(String message, Throwable cause) {
        super(message, "HORARIO_DUPLICADO", cause);
    }

    public static HorarioDuplicadoException porSolapamiento(Integer diaSemana) {
        return new HorarioDuplicadoException("El horario se solapa con un horario existente para el día: " + diaSemana);
    }

    public static HorarioDuplicadoException porSolapamiento(Integer diaSemana, String horaInicio, String horaFin) {
        return new HorarioDuplicadoException(
            "El horario se solapa con un horario existente. Día: " + diaSemana + 
            ", Hora inicio: " + horaInicio + ", Hora fin: " + horaFin
        );
    }
}
