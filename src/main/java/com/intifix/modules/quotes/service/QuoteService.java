package com.intifix.modules.quotes.service;

import com.intifix.modules.notifications.service.NotificationService;
import com.intifix.modules.quotes.dto.CreateQuoteRequest;
import com.intifix.modules.quotes.dto.QuoteDto;
import com.intifix.modules.quotes.entity.Cotizacion;
import com.intifix.modules.quotes.entity.EstadoCotizacion;
import com.intifix.modules.quotes.repository.CotizacionRepository;
import com.intifix.modules.services.entity.Servicio;
import com.intifix.modules.services.entity.enums.EstadoServicio;
import com.intifix.modules.services.repository.ServicioRepository;
import com.intifix.shared.dto.PageRequestDto;
import com.intifix.shared.dto.PageResponse;
import com.intifix.shared.events.DomainEvent;
import com.intifix.shared.events.DomainEventPublisher;
import com.intifix.shared.exception.ApiException;
import com.intifix.shared.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class QuoteService {

    private final CotizacionRepository cotizacionRepository;
    private final ServicioRepository servicioRepository;
    private final DomainEventPublisher eventPublisher;
    private final NotificationService notificationService;

    public QuoteService(
        CotizacionRepository cotizacionRepository,
        ServicioRepository servicioRepository,
        DomainEventPublisher eventPublisher,
        NotificationService notificationService
    ) {
        this.cotizacionRepository = cotizacionRepository;
        this.servicioRepository = servicioRepository;
        this.eventPublisher = eventPublisher;
        this.notificationService = notificationService;
    }

    @Transactional
    public QuoteDto submit(CreateQuoteRequest req) {
        UUID tecnicoId = SecurityUtils.currentUserId();
        Servicio servicio = servicioRepository.findById(req.servicioId())
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service not found"));

        if (servicio.getEstado() != EstadoServicio.PENDIENTE && servicio.getEstado() != EstadoServicio.COTIZANDO) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Service is not accepting quotes");
        }
        if (cotizacionRepository.existsByServicioIdAndTecnicoId(req.servicioId(), tecnicoId)) {
            throw new ApiException(HttpStatus.CONFLICT, "Quote already submitted");
        }

        Cotizacion q = new Cotizacion();
        q.setServicioId(req.servicioId());
        q.setTecnicoId(tecnicoId);
        q.setMonto(req.monto());
        q.setMensaje(req.mensaje());
        q.setValidezHasta(Instant.now());
        cotizacionRepository.save(q);

        if (servicio.getEstado() == EstadoServicio.PENDIENTE) {
            servicio.setEstado(EstadoServicio.COTIZANDO);
            servicioRepository.save(servicio);
        }

        eventPublisher.publish(DomainEvent.of(
            "QUOTE_SUBMITTED",
            "servicio",
            servicio.getIdServicio(),
            tecnicoId,
            q.getId()
        ));
        notificationService.notifyUser(
            servicio.getIdCliente(),
            "COTIZACION",
            "Nueva cotización recibida",
            "Un técnico envió una cotización para tu servicio"
        );

        return toDto(q);
    }

    @Transactional(readOnly = true)
    public PageResponse < QuoteDto > listForService(UUID servicioId, PageRequestDto page) {
        UUID clienteId = SecurityUtils.currentUserId();
        Servicio servicio = servicioRepository.findByIdServicioAndIdCliente(servicioId, clienteId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service not found"));

        Page < Cotizacion > result = cotizacionRepository.findByServicioId(
            servicio.getIdServicio(),
            PageRequest.of(page.page(), page.size())
        );
        return PageResponse.of(
            result.getContent().stream().map(this::toDto).toList(),
            page.page(),
            page.size(),
            result.getTotalElements()
        );
    }

    private QuoteDto toDto(Cotizacion q) {
        return new QuoteDto(
            q.getId(),
            q.getServicioId(),
            q.getTecnicoId(),
            q.getMonto(),
            "USD",
            q.getMensaje(),
            q.getEstado(),
            q.getValidezHasta(),
            q.getCreatedAt()
        );
    }
}
