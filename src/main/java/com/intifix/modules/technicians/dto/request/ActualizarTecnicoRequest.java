package com.intifix.modules.technicians.dto.request;

import com.intifix.modules.technicians.enums.DisponibilidadTecnico;
import com.intifix.modules.technicians.enums.EstadoAprobacionTecnico;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarTecnicoRequest {

    @Size(max = 255, message = "Los nombres completos no pueden exceder 255 caracteres")
    private String nombresCompletos;

    @Size(max = 20, message = "El DNI/RUC no puede exceder 20 caracteres")
    @Pattern(regexp = "^[0-9]{8,20}$", message = "El DNI/RUC debe contener solo números y tener entre 8 y 20 dígitos")
    private String dniRuc;

    @Min(value = 0, message = "Los años de experiencia no pueden ser negativos")
    @Max(value = 50, message = "Los años de experiencia no pueden exceder 50")
    private Integer experienciaAnios;

    private EstadoAprobacionTecnico estadoAprobacion;

    private DisponibilidadTecnico disponibilidad;

    @DecimalMin(value = "0.01", message = "La tarifa base debe ser mayor a 0")
    @DecimalMax(value = "99999.99", message = "La tarifa base no puede exceder 99999.99")
    private BigDecimal tarifaBase;

    @Size(max = 1000, message = "La URL del DNI frontal no puede exceder 1000 caracteres")
    @Pattern(regexp = "^(https?://|ftp://)?[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$|^$", message = "La URL del DNI frontal no es válida")
    private String dniFrontalUrl;

    @Size(max = 1000, message = "La URL del DNI trasero no puede exceder 1000 caracteres")
    @Pattern(regexp = "^(https?://|ftp://)?[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$|^$", message = "La URL del DNI trasero no es válida")
    private String dniTraseroUrl;

    @Size(max = 1000, message = "La URL del antecedente penal no puede exceder 1000 caracteres")
    @Pattern(regexp = "^(https?://|ftp://)?[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$|^$", message = "La URL del antecedente penal no es válida")
    private String antecedentePenalUrl;

    @Size(max = 1000, message = "La URL del certificado técnico no puede exceder 1000 caracteres")
    @Pattern(regexp = "^(https?://|ftp://)?[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$|^$", message = "La URL del certificado técnico no es válida")
    private String certificadoTecnicoUrl;

    // Sin patrón estricto: admite también URLs locales (http://localhost/uploads/...).
    @Size(max = 1000, message = "La URL de la foto de perfil no puede exceder 1000 caracteres")
    private String fotoPerfilUrl;

    private UUID idUbicacion;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @Pattern(regexp = "^[0-9+()\\-\\s]{6,20}$|^$", message = "El teléfono de contacto no es válido")
    private String telefonoContacto;
}
