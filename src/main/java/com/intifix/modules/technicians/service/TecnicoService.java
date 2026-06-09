package com.intifix.modules.technicians.service;

import com.intifix.modules.technicians.dto.*;
import java.math.BigDecimal;
import java.util.UUID;

public interface TecnicoService {
    TecnicoResponse inicializarPerfil(UUID usuarioId, String nombresCompletos, String dniRuc, BigDecimal tarifaBase, String dniFrontal, String dniTrasero, String antecedentes);
    TecnicoResponse obtenerPorId(UUID usuarioId);
    TecnicoResponse actualizarPerfil(UUID usuarioId, TechUpdateRequest request);
    TecnicoResponse actualizarDisponibilidad(UUID usuarioId, TechStatusUpdateRequest request);
    TecnicoResponse procesarAprobacion(UUID usuarioId, TechAprobacionRequest request);
    void incrementarServicioCompleto(UUID usuarioId, Double nuevaCalificacion);
}
