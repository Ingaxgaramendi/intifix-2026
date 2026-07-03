package com.intifix.modules.technicians.dto.response;

import com.intifix.modules.auth.entity.EstadoUsuario;
import com.intifix.modules.technicians.enums.DisponibilidadTecnico;
import com.intifix.modules.technicians.enums.EstadoAprobacionTecnico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TecnicoResponse {

    private UUID idUsuario;
    private String nombresCompletos;
    private String dniRuc;
    private Integer experienciaAnios;
    private EstadoAprobacionTecnico estadoAprobacion;
    private DisponibilidadTecnico disponibilidad;
    private BigDecimal tarifaBase;
    private String fotoPerfilUrl;
    private UUID idUbicacion;
    private String descripcion;
    private String telefonoContacto;
    private ZonedDateTime creadoEn;
    private EstadoUsuario estadoUsuario;
}
