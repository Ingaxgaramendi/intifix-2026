package com.intifix.modules.users.listener;

import com.intifix.modules.audit.event.UserCreatedEvent;
import com.intifix.modules.users.entity.PerfilCliente;
import com.intifix.modules.users.repository.PerfilClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Auto-aprovisiona el perfil de Cliente cuando se registra un usuario con rol
 * CLIENTE. El registro (módulo auth) solo crea la identidad; sin este perfil,
 * el dashboard, "pedir servicio" y el perfil fallaban con CLIENTE_NOT_FOUND.
 *
 * <p>Corre AFTER_COMMIT para que la fila de usuario ya esté persistida, y en una
 * transacción nueva. Como el registro no captura el nombre, se deriva uno
 * temporal del correo; el usuario lo edita después en su perfil.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteAutoProvisionListener {

    private final PerfilClienteRepository perfilClienteRepository;

    @TransactionalEventListener(phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onUserCreated(UserCreatedEvent event) {
        if (event.rol() == null || !event.rol().contains("CLIENTE")) {
            return;
        }
        if (perfilClienteRepository.existsById(event.userId())) {
            return;
        }

        perfilClienteRepository.save(PerfilCliente.builder()
                .idUsuario(event.userId())
                .nombresCompletos(nombreDesdeCorreo(event.email()))
                .dniRuc(event.dni())
                .build());

        log.info("Perfil de cliente auto-creado para usuario {}", event.userId());
    }

    private String nombreDesdeCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return "Nuevo Cliente";
        }
        String local = correo.split("@")[0].trim();
        return local.isBlank() ? "Nuevo Cliente" : local;
    }
}
