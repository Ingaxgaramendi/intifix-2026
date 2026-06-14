package com.intifix.modules.technicians.service;

import com.intifix.modules.technicians.dto.response.ReputacionResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface ReputacionTecnicoService {

    ReputacionResponse obtenerReputacion(UUID idUsuarioTecnico);

    ReputacionResponse actualizarReputacion(UUID idUsuarioTecnico, BigDecimal promedioCalificacion, Integer totalResenas);

    ReputacionResponse incrementarServiciosCompletados(UUID idUsuarioTecnico);

    ReputacionResponse actualizarPromedioCalificaciones(UUID idUsuarioTecnico, BigDecimal nuevaCalificacion);

    ReputacionResponse inicializarReputacion(UUID idUsuarioTecnico);

    boolean existeReputacion(UUID idUsuarioTecnico);
}
