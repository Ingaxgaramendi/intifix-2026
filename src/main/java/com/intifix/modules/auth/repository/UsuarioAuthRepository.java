package com.intifix.modules.auth.repository;

import com.intifix.modules.auth.entity.EstadoUsuario;
import com.intifix.modules.auth.entity.UsuarioAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioAuthRepository extends JpaRepository<UsuarioAuth, UUID>, JpaSpecificationExecutor<UsuarioAuth> {

    Optional<UsuarioAuth> findByCorreo(String correo);

    /**
     * Proyección ligera para el filtro JWT: verifica el estado de la cuenta
     * en cada request sin hidratar la entidad completa ni sus roles.
     */
    @Query("SELECT u.estado FROM UsuarioAuth u WHERE u.idUsuario = :idUsuario")
    Optional<EstadoUsuario> obtenerEstadoPorId(@Param("idUsuario") UUID idUsuario);

    boolean existsByCorreo(String correo);

    boolean existsByTelefono(String telefono);

    boolean existsByCorreoAndIdUsuarioNot(String correo, UUID idUsuario);

    boolean existsByTelefonoAndIdUsuarioNot(String telefono, UUID idUsuario);

    Optional<UsuarioAuth> findByEstado(EstadoUsuario estado);

    long countByEstado(EstadoUsuario estado);

    Optional<UsuarioAuth> findByIdUsuarioAndEstado(UUID idUsuario, EstadoUsuario estado);

    void deleteByIdUsuario(UUID idUsuario);
}
