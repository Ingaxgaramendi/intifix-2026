package com.intifix.modules.users.repository;

import com.intifix.modules.users.entity.PerfilCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PerfilClienteRepository extends JpaRepository < PerfilCliente, UUID > {}
