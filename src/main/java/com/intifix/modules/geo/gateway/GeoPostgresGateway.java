package com.intifix.modules.geo.gateway;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Puerto hacia los datos transaccionales en PostgreSQL que el módulo geo
 * necesita: la ubicación PÚBLICA del técnico (tablas {@code ubicaciones} y
 * {@code perfiles_tecnico.id_ubicacion}, ya existentes) y consultas de apoyo
 * para la búsqueda (especialidad, info del técnico, candidatos por bounding box).
 *
 * <p>geo no posee entidades JPA de esas tablas: las integra por UUID, lo que
 * mantiene el módulo desacoplado y portable a microservicio.</p>
 */
public interface GeoPostgresGateway {

    boolean existeTecnico(UUID idTecnico);

    /** Crea una fila en {@code ubicaciones} y devuelve su id generado. */
    UUID crearUbicacion(DatosUbicacion datos);

    void actualizarUbicacion(UUID idUbicacion, DatosUbicacion datos);

    /** Asocia la ubicación pública al perfil del técnico. */
    void vincularUbicacionAPerfil(UUID idTecnico, UUID idUbicacion);

    Optional<UbicacionPublica> obtenerUbicacionPublica(UUID idTecnico);

    /** Una ubicación por su id (tabla {@code ubicaciones}), sin pasar por el perfil del técnico. */
    Optional<UbicacionPublica> obtenerPorId(UUID idUbicacion);

    /** UUIDs de técnicos aprobados que tienen una especialidad dada. */
    Set<UUID> tecnicosConEspecialidad(UUID idEspecialidad);

    /**
     * Candidatos por ubicación PÚBLICA dentro de un bounding box (filtro grueso
     * en SQL). La distancia exacta y el orden los aplica el servicio con
     * {@code GeoUtils}. Solo técnicos APROBADOS con ubicación registrada.
     */
    List<TecnicoPublico> candidatosPublicosEnArea(double latMin, double latMax,
                                                  double lngMin, double lngMax,
                                                  UUID idEspecialidadOpcional);

    /** Enriquecimiento: nombre y tarifa base por técnico. */
    Map<UUID, TecnicoInfo> obtenerInfo(Collection<UUID> idsTecnicos);

    record DatosUbicacion(String departamento, String provincia, String distrito,
                          String direccionTexto, String referencia,
                          BigDecimal latitud, BigDecimal longitud) {}

    record UbicacionPublica(UUID idUbicacion, String departamento, String provincia,
                            String distrito, String direccionTexto, String referencia,
                            BigDecimal latitud, BigDecimal longitud) {}

    record TecnicoPublico(UUID idTecnico, String nombresCompletos, BigDecimal tarifaBase,
                          double latitud, double longitud) {}

    record TecnicoInfo(UUID idTecnico, String nombresCompletos, BigDecimal tarifaBase) {}
}
