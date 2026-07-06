package com.intifix.modules.audit.event;

import java.util.UUID;

public record CertificadoAprobadoEvent(
        UUID idTecnico,
        UUID idEspecialidad,
        String nombreEspecialidad
) {}
