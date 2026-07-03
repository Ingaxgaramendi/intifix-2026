package com.intifix.modules.apelaciones.service;

import com.intifix.modules.apelaciones.dto.ApelacionResponse;
import com.intifix.modules.apelaciones.dto.CrearApelacionRequest;
import com.intifix.modules.apelaciones.dto.RevisarApelacionRequest;
import com.intifix.modules.apelaciones.entity.EstadoApelacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ApelacionService {
    void crear(CrearApelacionRequest request);
    Page<ApelacionResponse> listar(EstadoApelacion estado, Pageable pageable);
    ApelacionResponse revisar(UUID idApelacion, RevisarApelacionRequest request);
    long contarPendientes();
}
