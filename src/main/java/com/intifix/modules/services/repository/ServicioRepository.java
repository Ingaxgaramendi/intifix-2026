package com.intifix.modules.services.repository;

import com.intifix.modules.services.entity.Servicio;
import com.intifix.modules.services.entity.enums.EstadoServicio; // Importante importar tu enum externo
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ServicioRepository extends JpaRepository < Servicio, UUID > {

    // CORRECCIÓN AQUÍ: El parámetro debe ser de tipo EstadoServicio (el Enum), no String ni name()
    List < Servicio > findByEstado(EstadoServicio estado);

    List < Servicio > findByIdCliente(UUID idCliente);
}
