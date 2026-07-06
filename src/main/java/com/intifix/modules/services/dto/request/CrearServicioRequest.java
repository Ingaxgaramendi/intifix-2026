package com.intifix.modules.services.dto.request;

import com.intifix.modules.services.enums.ModalidadServicio;
import com.intifix.modules.services.enums.TipoFecha;
import com.intifix.modules.services.enums.TipoSolicitud;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating a new service.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearServicioRequest {

    // idCliente removed - obtained from SecurityContextHolder to prevent IDOR

    // Requerida solo para EN_CASA_CLIENTE (se valida en el servicio según modalidad).
    private UUID idUbicacion;

    @NotNull(message = "La especialidad es obligatoria")
    private UUID idEspecialidad;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 255, message = "El título debe tener entre 5 y 255 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String descripcion;

    @NotNull(message = "La modalidad es obligatoria")
    private ModalidadServicio modalidad;

    @NotNull(message = "El presupuesto del cliente es obligatorio")
    @DecimalMin(value = "0.01", message = "El presupuesto debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El presupuesto no puede exceder 999,999.99")
    private BigDecimal presupuestoMaximo;

    /** Scheduling mode; defaults to EXACTA if null. */
    private TipoFecha tipoFecha;

    /** Required for EXACTA mode. Validated in service layer. */
    private ZonedDateTime fechaProgramada;

    /** Required for RANGO mode: start of the window. */
    private ZonedDateTime fechaInicioRango;

    /** Required for RANGO mode: end of the window (max 5 days after fechaInicioRango). */
    private ZonedDateTime fechaFinRango;

    @NotEmpty(message = "Agrega al menos una foto")
    @Size(max = 5, message = "Puedes agregar como máximo 5 fotos")
    private List<@NotBlank(message = "URL de foto inválida") String> fotos;

    /** PUBLICA (defecto) o DIRECTA. Cuando es DIRECTA, idTecnicoDirecto es obligatorio. */
    private TipoSolicitud tipoSolicitud;

    /** Solo para DIRECTA: UUID del técnico elegido por el cliente. */
    private UUID idTecnicoDirecto;
}
