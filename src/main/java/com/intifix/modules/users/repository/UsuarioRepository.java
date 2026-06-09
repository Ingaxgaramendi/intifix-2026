package com.intifix.modules.users.repository;

import com.intifix.modules.users.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository < Usuario, UUID > {
    Optional < Usuario > findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    boolean existsByTelefono(String telefono);
}
