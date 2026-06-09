package com.intifix.modules.payments.service;

import com.intifix.modules.notifications.service.NotificationService;
import com.intifix.modules.payments.dto.CreatePaymentRequest;
import com.intifix.modules.payments.dto.PaymentDto;
import com.intifix.modules.payments.entity.EstadoPago;
import com.intifix.modules.payments.entity.Factura;
import com.intifix.modules.payments.entity.Pago;
import com.intifix.modules.payments.repository.FacturaRepository;
import com.intifix.modules.payments.repository.PagoRepository;
import com.intifix.modules.services.entity.Servicio;
import com.intifix.modules.services.repository.ServicioRepository;
import com.intifix.shared.dto.PageRequestDto;
import com.intifix.shared.dto.PageResponse;
import com.intifix.shared.exception.ApiException;
import com.intifix.shared.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentService {

    private static final AtomicLong INVOICE_SEQ = new AtomicLong(System.currentTimeMillis());

    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;
    private final ServicioRepository servicioRepository;
    private final NotificationService notificationService;

    public PaymentService(
            PagoRepository pagoRepository,
            FacturaRepository facturaRepository,
            ServicioRepository servicioRepository,
            NotificationService notificationService
    ) {
        this.pagoRepository = pagoRepository;
        this.facturaRepository = facturaRepository;
        this.servicioRepository = servicioRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public PaymentDto create(CreatePaymentRequest req) {
        UUID clienteId = SecurityUtils.currentUserId();
        Servicio servicio = servicioRepository.findByIdServicioAndIdCliente(req.servicioId(), clienteId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service not found"));

        Pago pago = new Pago();
        pago.setServicioId(servicio.getIdServicio());
        pago.setMonto(req.monto());
        pago.setMetodoPagoId(req.metodoPagoId());
        pago.setEstado(EstadoPago.PENDIENTE);
        pagoRepository.save(pago);
        return toDto(pago, null);
    }

    @Transactional
    public PaymentDto markPaid(UUID pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Payment not found"));
        if (pago.getEstado() == EstadoPago.PAGADO) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Payment already paid");
        }
        pago.setEstado(EstadoPago.PAGADO);
        pago.setPagadoAt(Instant.now());
        pago.setReferencia("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pagoRepository.save(pago);

        Factura factura = buildInvoice(pago);
        facturaRepository.save(factura);

        Servicio servicio = servicioRepository.findById(pago.getServicioId()).orElse(null);
        if (servicio != null) {
            notificationService.notifyUser(
                    servicio.getIdCliente(),
                    "PAGO",
                    "Pago confirmado",
                    "Tu pago fue procesado correctamente"
            );
        }
        return toDto(pago, factura);
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentDto> myPayments(PageRequestDto page) {
        UUID clienteId = SecurityUtils.currentUserId();
        Page<Pago> result = pagoRepository.findAll(PageRequest.of(page.page(), page.size()));
        return PageResponse.of(
                result.getContent().stream()
                        .filter(p -> servicioRepository.findById(p.getServicioId())
                                .map(s -> s.getIdCliente().equals(clienteId)).orElse(false))
                        .map(p -> toDto(p, facturaRepository.findByPagoId(p.getId()).orElse(null)))
                        .toList(),
                page.page(),
                page.size(),
                result.getTotalElements()
        );
    }

    private Factura buildInvoice(Pago pago) {
        Factura f = new Factura();
        f.setPagoId(pago.getId());
        f.setNumero("INV-" + INVOICE_SEQ.incrementAndGet());
        f.setPdfUrl("/invoices/" + f.getNumero() + ".pdf");
        return f;
    }

    private PaymentDto toDto(Pago pago, Factura factura) {
        PaymentDto.InvoiceDto invoiceDto = factura == null ? null : new PaymentDto.InvoiceDto(
                factura.getId(),
                factura.getNumero(),
                factura.getTotal(),
                factura.getPdfUrl()
        );
        return new PaymentDto(
                pago.getId(),
                pago.getServicioId(),
                pago.getMonto(),
                "USD",
                pago.getEstado(),
                pago.getReferencia(),
                pago.getPagadoAt(),
                invoiceDto
        );
    }
}
