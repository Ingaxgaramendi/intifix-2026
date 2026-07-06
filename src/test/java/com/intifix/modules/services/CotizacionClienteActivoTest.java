package com.intifix.modules.services;

import com.intifix.modules.services.dto.request.CrearCotizacionRequest;
import com.intifix.modules.services.entity.Servicio;
import com.intifix.modules.services.enums.EstadoServicio;
import com.intifix.modules.services.exception.ClienteNoActivoException;
import com.intifix.modules.services.exception.ServicioNoEncontradoException;
import com.intifix.modules.services.gateway.TechnicianGateway;
import com.intifix.modules.services.gateway.UserGateway;
import com.intifix.modules.services.mapper.CotizacionMapper;
import com.intifix.modules.services.repository.CotizacionRepository;
import com.intifix.modules.services.repository.ServicioRepository;
import com.intifix.modules.services.service.impl.CotizacionServiceImpl;
import com.intifix.shared.security.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CotizacionClienteActivoTest {

    @Mock CotizacionRepository cotizacionRepository;
    @Mock ServicioRepository servicioRepository;
    @Mock CotizacionMapper cotizacionMapper;
    @Mock TechnicianGateway technicianGateway;
    @Mock UserGateway userGateway;
    @Mock ApplicationEventPublisher eventPublisher;

    CotizacionServiceImpl service;
    UUID idTecnico;
    UUID idCliente;
    UUID idServicio;

    @BeforeEach
    void setUp() {
        service = new CotizacionServiceImpl(
            cotizacionRepository, servicioRepository, cotizacionMapper,
            technicianGateway, userGateway, eventPublisher
        );

        idTecnico = UUID.randomUUID();
        idCliente = UUID.randomUUID();
        idServicio = UUID.randomUUID();

        AuthenticatedUser principal = AuthenticatedUser.builder()
            .id(idTecnico)
            .correo("tecnico@test.com")
            .roles(Set.of("TECNICO"))
            .build();
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(principal, null, Set.of())
        );
    }

    @Test
    void crearCotizacion_lanzaClienteNoActivo_cuandoClienteSuspendido() {
        Servicio servicio = Servicio.builder()
            .idServicio(idServicio)
            .idCliente(idCliente)
            .estado(EstadoServicio.PENDIENTE)
            .build();
        when(servicioRepository.findById(idServicio)).thenReturn(Optional.of(servicio));
        when(userGateway.isUserActive(idCliente)).thenReturn(false);

        CrearCotizacionRequest request = new CrearCotizacionRequest();
        request.setIdServicio(idServicio);
        request.setPrecio(java.math.BigDecimal.valueOf(150));
        request.setTiempoEstimado("2 horas");

        assertThatThrownBy(() -> service.crearCotizacion(request))
            .isInstanceOf(ClienteNoActivoException.class)
            .hasMessageContaining(idServicio.toString());
    }

    @Test
    void crearCotizacion_lanzaServicioNoEncontrado_cuandoIdInexistente() {
        UUID idInexistente = UUID.randomUUID();
        when(servicioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        CrearCotizacionRequest request = new CrearCotizacionRequest();
        request.setIdServicio(idInexistente);
        request.setPrecio(java.math.BigDecimal.valueOf(100));
        request.setTiempoEstimado("1 hora");

        assertThatThrownBy(() -> service.crearCotizacion(request))
            .isInstanceOf(ServicioNoEncontradoException.class);
    }

    @Test
    void crearCotizacion_continuaSiClienteActivo() {
        Servicio servicio = Servicio.builder()
            .idServicio(idServicio)
            .idCliente(idCliente)
            .estado(EstadoServicio.PENDIENTE)
            .build();
        when(servicioRepository.findById(idServicio)).thenReturn(Optional.of(servicio));
        when(userGateway.isUserActive(idCliente)).thenReturn(true);
        when(technicianGateway.existsTechnician(idTecnico)).thenReturn(true);
        when(technicianGateway.isApproved(idTecnico)).thenReturn(true);
        when(technicianGateway.isAvailable(idTecnico)).thenReturn(true);

        // No lanza ClienteNoActivoException — continúa hacia la validación del técnico
        // (que sí está aprobado/disponible en este stub).
        // El mapper devuelve null pero lo que validamos es que NO se lanza la excepción de cliente.
        CrearCotizacionRequest request = new CrearCotizacionRequest();
        request.setIdServicio(idServicio);
        request.setPrecio(java.math.BigDecimal.valueOf(150));
        request.setTiempoEstimado("2 horas");

        // Se llegará a cotizacionMapper.toEntity(request) que devuelve null → NPE en la asignación.
        // Eso confirma que el guard de cliente activo se superó sin lanzar ClienteNoActivoException.
        assertThatThrownBy(() -> service.crearCotizacion(request))
            .isNotInstanceOf(ClienteNoActivoException.class);
    }
}
