package com.intifix.modules.services.service;

import com.intifix.modules.services.dto.request.CrearEvidenciaRequest;
import com.intifix.modules.services.dto.response.EvidenciaServicioResponse;
import com.intifix.modules.services.enums.TipoArchivo;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for EvidenciaServicio operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public interface EvidenciaService {

    EvidenciaServicioResponse crearEvidencia(CrearEvidenciaRequest request);

    void eliminarEvidencia(UUID idEvidencia);

    EvidenciaServicioResponse obtenerEvidenciaPorId(UUID idEvidencia);

    List<EvidenciaServicioResponse> obtenerEvidenciasPorServicio(UUID idServicio);

    List<EvidenciaServicioResponse> obtenerEvidenciasPorUsuario(UUID subidoPor);

    List<EvidenciaServicioResponse> obtenerEvidenciasPorServicioYTipo(UUID idServicio, TipoArchivo tipoArchivo);

    long contarEvidenciasPorServicio(UUID idServicio);

    long contarEvidenciasPorUsuario(UUID subidoPor);

    boolean existeEvidencia(UUID idEvidencia);
}
