package com.intifix.modules.users.repository;

import com.intifix.modules.users.entity.PerfilCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * La PK de {@link PerfilCliente} es {@code idUsuario}: las búsquedas por id
 * se resuelven con {@code findById / existsById / count} heredados de
 * {@link JpaRepository}. Aquí solo viven consultas con valor real de negocio.
 */
@Repository
public interface PerfilClienteRepository extends JpaRepository<PerfilCliente, UUID> {

    Optional<PerfilCliente> findByDniRuc(String dniRuc);

    boolean existsByDniRuc(String dniRuc);

    boolean existsByDniRucAndIdUsuarioNot(String dniRuc, UUID idUsuario);

    @Query("SELECT p FROM PerfilCliente p WHERE LOWER(p.nombresCompletos) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    Page<PerfilCliente> buscarPorNombre(@Param("nombre") String nombre, Pageable pageable);
}
