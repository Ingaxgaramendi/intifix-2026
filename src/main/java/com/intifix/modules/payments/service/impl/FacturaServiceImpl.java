package com.intifix.modules.payments.service.impl;

import com.intifix.modules.payments.dto.request.CrearFacturaRequest;
import com.intifix.modules.payments.dto.response.FacturaResponse;
import com.intifix.modules.payments.entity.EstadoFiscalComprobante;
import com.intifix.modules.payments.entity.Factura;
import com.intifix.modules.payments.entity.TipoComprobante;
import com.intifix.modules.payments.event.FacturaEmitidaEvent;
import com.intifix.modules.audit.event.InvoiceGeneratedEvent;
import com.intifix.modules.payments.exception.FacturaNoEncontradaException;
import com.intifix.modules.payments.mapper.FacturaMapper;
import com.intifix.modules.payments.repository.FacturaRepository;
import com.intifix.modules.payments.repository.PagoRepository;
import com.intifix.modules.payments.service.interfaces.FacturaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepository;
    private final PagoRepository pagoRepository;
    private final FacturaMapper facturaMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public FacturaResponse crearFactura(CrearFacturaRequest request) {
        log.info("Creando factura para pago: {}", request.getIdPago());

        validarPago(request.getIdPago());
        validarNoFacturaDuplicada(request.getIdPago());

        if (request.getCodigoComprobante() == null || request.getCodigoComprobante().isBlank()) {
            request.setCodigoComprobante(generarCodigoComprobante(request.getTipo()));
        }

        Factura factura = facturaMapper.toEntity(request);
        Factura facturaGuardada = facturaRepository.save(factura);

        eventPublisher.publishEvent(new FacturaEmitidaEvent(
                this,
                facturaGuardada.getIdFactura(),
                facturaGuardada.getIdPago(),
                null,
                facturaGuardada.getCodigoComprobante(),
                facturaGuardada.getTipo(),
                BigDecimal.ZERO,
                facturaGuardada.getFechaEmision()
        ));

        // Auditoría desacoplada de la emisión de comprobante.
        eventPublisher.publishEvent(new InvoiceGeneratedEvent(
                facturaGuardada.getIdFactura(),
                facturaGuardada.getIdPago(),
                null
        ));

        log.info("Factura creada exitosamente con ID: {}", facturaGuardada.getIdFactura());
        return facturaMapper.toResponse(facturaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaResponse obtenerFacturaPorId(UUID idFactura) {
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new FacturaNoEncontradaException(idFactura));
        return facturaMapper.toResponse(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaResponse obtenerFacturaPorPago(UUID idPago) {
        Factura factura = facturaRepository.findByIdPago(idPago)
                .orElseThrow(() -> new FacturaNoEncontradaException("No existe factura para el pago: " + idPago));
        return facturaMapper.toResponse(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaResponse obtenerFacturaPorCodigo(String codigoComprobante) {
        Factura factura = facturaRepository.findByCodigoComprobante(codigoComprobante)
                .orElseThrow(() -> new FacturaNoEncontradaException("Factura no encontrada con código: " + codigoComprobante));
        return facturaMapper.toResponse(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturaResponse> listarFacturas() {
        return facturaRepository.findAll().stream()
                .map(facturaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturaResponse> listarFacturasPorEstado(String estadoFiscal) {
        EstadoFiscalComprobante estado = EstadoFiscalComprobante.valueOf(estadoFiscal.toUpperCase());
        return facturaRepository.findByEstadoFiscal(estado).stream()
                .map(facturaMapper::toResponse)
                .toList();
    }

    private void validarPago(UUID idPago) {
        if (!pagoRepository.existsById(idPago)) {
            throw new FacturaNoEncontradaException("Pago no encontrado: " + idPago);
        }
    }

    private void validarNoFacturaDuplicada(UUID idPago) {
        if (facturaRepository.findByIdPago(idPago).isPresent()) {
            throw new FacturaNoEncontradaException("Ya existe una factura para el pago: " + idPago);
        }
    }

    private String generarCodigoComprobante(TipoComprobante tipo) {
        String prefijo = switch (tipo) {
            case BOLETA -> "B";
            case FACTURA -> "F";
            case NOTA_CREDITO -> "NC";
        };
        return prefijo + "-" + System.currentTimeMillis();
    }
}
