package com.intifix.modules.technicians.listener;

import com.intifix.modules.audit.event.UserCreatedEvent;
import com.intifix.modules.technicians.entity.PerfilTecnico;
import com.intifix.modules.technicians.repository.PerfilTecnicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

/**
 * Auto-aprovisiona el perfil de Técnico cuando se registra un usuario con rol
 * TECNICO. El registro (módulo auth) solo crea la identidad; sin este perfil el
 * técnico no tendría DNI, tarifa ni estado de aprobación.
 *
 * <p>Corre AFTER_COMMIT y en transacción nueva, igual que el de cliente. Los
 * campos obligatorios que aún no se piden en el registro (experiencia, tarifa)
 * se inicializan en cero y el técnico los completa luego en su perfil; queda
 * PENDIENTE de aprobación.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TecnicoAutoProvisionListener {

    private final PerfilTecnicoRepository perfilTecnicoRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onUserCreated(UserCreatedEvent event) {
        if (event.rol() == null || !event.rol().contains("TECNICO")) {
            return;
        }
        if (perfilTecnicoRepository.existsByIdUsuario(event.userId())) {
            return;
        }

        // Las URLs de documentos son NOT NULL en la BD pero aún no se piden en el
        // registro: se inicializan vacías y el técnico las sube luego en su perfil.
        perfilTecnicoRepository.save(PerfilTecnico.builder()
                .idUsuario(event.userId())
                .nombresCompletos(nombreDesdeCorreo(event.email()))
                .dniRuc(event.dni())
                .experienciaAnios(0)
                .tarifaBase(BigDecimal.ZERO)
                .dniFrontalUrl("")
                .dniTraseroUrl("")
                .antecedentePenalUrl("")
                .build());

        log.info("Perfil de técnico auto-creado para usuario {}", event.userId());
    }

    private String nombreDesdeCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return "Nuevo Técnico";
        }
        String local = correo.split("@")[0].trim();
        return local.isBlank() ? "Nuevo Técnico" : local;
    }
}
