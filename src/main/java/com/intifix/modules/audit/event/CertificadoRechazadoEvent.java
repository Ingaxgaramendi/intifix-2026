package com.intifix.modules.audit.event;

import java.util.UUID;

public record CertificadoRechazadoEvent(
        UUID idTecnico,
        UUID idEspecialidad,
        String nombreEspecialidad
) {}
