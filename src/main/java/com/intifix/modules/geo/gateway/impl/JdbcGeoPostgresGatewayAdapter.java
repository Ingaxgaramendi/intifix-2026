package com.intifix.modules.geo.gateway.impl;

import com.intifix.modules.geo.gateway.GeoPostgresGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JdbcGeoPostgresGatewayAdapter implements GeoPostgresGateway {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean existeTecnico(UUID idTecnico) {
        if (idTecnico == null) {
            return false;
        }
        Boolean existe = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM perfiles_tecnico WHERE id_usuario = ?)",
                Boolean.class, idTecnico);
        return Boolean.TRUE.equals(existe);
    }

    @Override
    public UUID crearUbicacion(DatosUbicacion d) {
        return jdbcTemplate.queryForObject("""
                INSERT INTO ubicaciones
                    (departamento, provincia, distrito, direccion_texto, referencia, latitud, longitud)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id_ubicacion
                """,
                (rs, n) -> rs.getObject("id_ubicacion", UUID.class),
                d.departamento(), d.provincia(), d.distrito(), d.direccionTexto(),
                d.referencia(), d.latitud(), d.longitud());
    }

    @Override
    public void actualizarUbicacion(UUID idUbicacion, DatosUbicacion d) {
        jdbcTemplate.update("""
                UPDATE ubicaciones SET
                    departamento = ?, provincia = ?, distrito = ?, direccion_texto = ?,
                    referencia = ?, latitud = ?, longitud = ?
                WHERE id_ubicacion = ?
                """,
                d.departamento(), d.provincia(), d.distrito(), d.direccionTexto(),
                d.referencia(), d.latitud(), d.longitud(), idUbicacion);
    }

    @Override
    public void vincularUbicacionAPerfil(UUID idTecnico, UUID idUbicacion) {
        jdbcTemplate.update(
                "UPDATE perfiles_tecnico SET id_ubicacion = ? WHERE id_usuario = ?",
                idUbicacion, idTecnico);
    }

    @Override
    public Optional<UbicacionPublica> obtenerUbicacionPublica(UUID idTecnico) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    SELECT u.id_ubicacion, u.departamento, u.provincia, u.distrito,
                           u.direccion_texto, u.referencia, u.latitud, u.longitud
                    FROM perfiles_tecnico pt
                    JOIN ubicaciones u ON u.id_ubicacion = pt.id_ubicacion
                    WHERE pt.id_usuario = ?
                    """,
                    (rs, n) -> new UbicacionPublica(
                            rs.getObject("id_ubicacion", UUID.class),
                            rs.getString("departamento"),
                            rs.getString("provincia"),
                            rs.getString("distrito"),
                            rs.getString("direccion_texto"),
                            rs.getString("referencia"),
                            rs.getBigDecimal("latitud"),
                            rs.getBigDecimal("longitud")),
                    idTecnico));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UbicacionPublica> obtenerPorId(UUID idUbicacion) {
        if (idUbicacion == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    SELECT id_ubicacion, departamento, provincia, distrito,
                           direccion_texto, referencia, latitud, longitud
                    FROM ubicaciones
                    WHERE id_ubicacion = ?
                    """,
                    (rs, n) -> new UbicacionPublica(
                            rs.getObject("id_ubicacion", UUID.class),
                            rs.getString("departamento"),
                            rs.getString("provincia"),
                            rs.getString("distrito"),
                            rs.getString("direccion_texto"),
                            rs.getString("referencia"),
                            rs.getBigDecimal("latitud"),
                            rs.getBigDecimal("longitud")),
                    idUbicacion));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Set<UUID> tecnicosConEspecialidad(UUID idEspecialidad) {
        List<UUID> ids = jdbcTemplate.query("""
                SELECT te.id_usuario_tecnico
                FROM tecnico_especialidad te
                JOIN perfiles_tecnico pt ON pt.id_usuario = te.id_usuario_tecnico
                WHERE te.id_especialidad = ? AND pt.estado_aprobacion = 'APROBADO'
                """,
                (rs, n) -> rs.getObject("id_usuario_tecnico", UUID.class),
                idEspecialidad);
        return new HashSet<>(ids);
    }

    @Override
    public List<TecnicoPublico> candidatosPublicosEnArea(double latMin, double latMax,
                                                         double lngMin, double lngMax,
                                                         UUID idEspecialidadOpcional) {
        StringBuilder sql = new StringBuilder("""
                SELECT pt.id_usuario, pt.nombres_completos, pt.tarifa_base, u.latitud, u.longitud
                FROM perfiles_tecnico pt
                JOIN ubicaciones u ON u.id_ubicacion = pt.id_ubicacion
                WHERE pt.estado_aprobacion = 'APROBADO'
                  AND u.latitud BETWEEN ? AND ?
                  AND u.longitud BETWEEN ? AND ?
                """);
        Object[] params;
        if (idEspecialidadOpcional != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM tecnico_especialidad te ")
               .append("WHERE te.id_usuario_tecnico = pt.id_usuario AND te.id_especialidad = ?)");
            params = new Object[]{latMin, latMax, lngMin, lngMax, idEspecialidadOpcional};
        } else {
            params = new Object[]{latMin, latMax, lngMin, lngMax};
        }
        return jdbcTemplate.query(sql.toString(),
                (rs, n) -> new TecnicoPublico(
                        rs.getObject("id_usuario", UUID.class),
                        rs.getString("nombres_completos"),
                        rs.getBigDecimal("tarifa_base"),
                        rs.getDouble("latitud"),
                        rs.getDouble("longitud")),
                params);
    }

    @Override
    public Map<UUID, TecnicoInfo> obtenerInfo(Collection<UUID> idsTecnicos) {
        if (idsTecnicos == null || idsTecnicos.isEmpty()) {
            return Map.of();
        }
        String placeholders = idsTecnicos.stream().map(x -> "?").collect(Collectors.joining(","));
        String sql = "SELECT id_usuario, nombres_completos, tarifa_base FROM perfiles_tecnico "
                + "WHERE id_usuario IN (" + placeholders + ")";
        List<TecnicoInfo> filas = jdbcTemplate.query(sql,
                (rs, n) -> new TecnicoInfo(
                        rs.getObject("id_usuario", UUID.class),
                        rs.getString("nombres_completos"),
                        rs.getBigDecimal("tarifa_base")),
                idsTecnicos.toArray());
        Map<UUID, TecnicoInfo> mapa = new HashMap<>();
        filas.forEach(f -> mapa.put(f.idTecnico(), f));
        return mapa;
    }
}
