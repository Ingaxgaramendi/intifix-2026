package com.intifix.modules.ai.dto;

import com.intifix.modules.technicians.dto.response.TecnicoResponse;
import com.intifix.modules.technicians.dto.response.ReputacionResponse;
import com.intifix.modules.technicians.enums.DisponibilidadTecnico;

import java.math.BigDecimal;

/**
 * DTO plano para el motor de IA.
 *
 * urlPerfil viene pre-construida desde el backend para que GPT-4o nunca
 * tenga que extraer ni ensamblar un UUID: solo copia el valor de urlPerfil
 * al crear el link markdown. Sin este campo el modelo alucina UUIDs.
 */
public record TecnicoRankeado(
        String urlPerfil,
        String nombre,
        BigDecimal tarifaBase,
        Integer experienciaAnios,
        DisponibilidadTecnico disponibilidad,
        BigDecimal promedioCalificacion,
        Integer totalResenas,
        Integer totalServicios
) {
    @SuppressWarnings("java:S1075")
    private static final String BASE_PATH = "/cliente/tecnicos/";

    public static TecnicoRankeado of(TecnicoResponse t, ReputacionResponse rep) {
        return new TecnicoRankeado(
                BASE_PATH + t.getIdUsuario(),
                t.getNombresCompletos(),
                t.getTarifaBase(),
                t.getExperienciaAnios(),
                t.getDisponibilidad(),
                rep.getPromedioCalificacion(),
                rep.getTotalResenas(),
                rep.getTotalServicios()
        );
    }

    public static TecnicoRankeado sinReputacion(TecnicoResponse t) {
        return new TecnicoRankeado(
                BASE_PATH + t.getIdUsuario(),
                t.getNombresCompletos(),
                t.getTarifaBase(),
                t.getExperienciaAnios(),
                t.getDisponibilidad(),
                BigDecimal.ZERO,
                0,
                0
        );
    }
}
