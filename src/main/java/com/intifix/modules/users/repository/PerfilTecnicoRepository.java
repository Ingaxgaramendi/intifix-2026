package com.intifix.modules.users.repository;

import com.intifix.modules.users.entity.PerfilTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PerfilTecnicoRepository extends JpaRepository < PerfilTecnico, UUID > {
    boolean existsByDniRuc(String dniRuc);
}
