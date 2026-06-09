package com.intifix.auth;

import com.intifix.modules.users.entity.RolUsuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthRegistrationPolicyTest {

    @Test
    void allowsClienteAndTecnico() {
        assertTrue(isRegistrationAllowed(RolUsuario.CLIENTE));
        assertTrue(isRegistrationAllowed(RolUsuario.TECNICO));
    }

    @Test
    void rejectsAdminSelfRegistration() {
        assertFalse(isRegistrationAllowed(RolUsuario.ADMIN));
    }

    private boolean isRegistrationAllowed(RolUsuario rol) {
        return rol == RolUsuario.CLIENTE || rol == RolUsuario.TECNICO;
    }
}
