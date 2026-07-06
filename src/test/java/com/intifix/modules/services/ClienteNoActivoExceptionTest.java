package com.intifix.modules.services;

import com.intifix.modules.services.exception.ClienteNoActivoException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteNoActivoExceptionTest {

    @Test
    void mensajeContieneIdDelServicio() {
        UUID idServicio = UUID.randomUUID();

        ClienteNoActivoException ex = ClienteNoActivoException.forServicio(idServicio);

        assertThat(ex.getMessage()).contains(idServicio.toString());
        assertThat(ex.getMessage()).contains("no está disponible");
    }

    @Test
    void errorCodeEsClienteNoActivo() {
        UUID idServicio = UUID.randomUUID();

        ClienteNoActivoException ex = ClienteNoActivoException.forServicio(idServicio);

        assertThat(ex.getErrorCode()).isEqualTo("CLIENTE_NO_ACTIVO");
    }

    @Test
    void factoryMethodEsEquivalenteAConstructor() {
        UUID idServicio = UUID.randomUUID();

        ClienteNoActivoException viaFactory = ClienteNoActivoException.forServicio(idServicio);
        ClienteNoActivoException viaConstructor = new ClienteNoActivoException(idServicio);

        assertThat(viaFactory.getMessage()).isEqualTo(viaConstructor.getMessage());
        assertThat(viaFactory.getErrorCode()).isEqualTo(viaConstructor.getErrorCode());
    }
}
