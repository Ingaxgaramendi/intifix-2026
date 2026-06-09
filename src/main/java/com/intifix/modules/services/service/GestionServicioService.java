package com.intifix.modules.services.service;

import com.intifix.modules.services.dto.*;
import java.util.List;
import java.util.UUID;

public interface GestionServicioService {
    ServicioResponse publicarServicio(ServicioRequest request);
    void registrarAsignacion(UUID idServicio, UUID idTecnico, UUID idCotizacion);
    void subirEvidenciaServicio(UUID idServicio, EvidenciaRequest request);
    void calificarYFinalizarServicio(UUID idServicio, CalificacionRequest request);
    void actualizarEstadoManual(UUID idServicio, String nuevoEstado, String comentario);
    List < ServicioResponse > listarDisponiblesParaCotizar();
}
