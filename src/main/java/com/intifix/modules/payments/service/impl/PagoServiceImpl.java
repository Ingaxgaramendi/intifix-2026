package com.intifix.modules.payments.service.impl;

import com.intifix.modules.payments.dto.request.CrearFacturaRequest;
import com.intifix.modules.payments.dto.request.CrearPagoRequest;
import com.intifix.modules.payments.dto.request.ProcesarPagoRequest;
import com.intifix.modules.payments.dto.request.ReembolsarPagoRequest;
import com.intifix.modules.payments.entity.TipoComprobante;
import com.intifix.modules.payments.dto.response.PagoDetalleResponse;
import com.intifix.modules.payments.dto.response.PagoResponse;
import com.intifix.modules.payments.dto.response.ResumenPagoResponse;
import com.intifix.modules.payments.entity.EstadoPago;
import com.intifix.modules.payments.entity.Pago;
import com.intifix.modules.payments.event.PagoCreadoEvent;
import com.intifix.modules.payments.event.PagoConfirmadoEvent;
import com.intifix.modules.payments.event.PagoFallidoEvent;
import com.intifix.modules.payments.event.PagoReembolsadoEvent;
import com.intifix.modules.payments.exception.EstadoPagoInvalidoException;
import com.intifix.modules.payments.exception.MetodoPagoNoEncontradoException;
import com.intifix.modules.payments.exception.MontoInvalidoException;
import com.intifix.modules.payments.exception.PagoDuplicadoException;
import com.intifix.modules.payments.exception.PagoNoEncontradoException;
import com.intifix.modules.payments.exception.ReembolsoNoPermitidoException;
import com.intifix.modules.payments.gateway.ServiceGateway;
import com.intifix.modules.payments.mapper.PagoMapper;
import com.intifix.modules.payments.provider.PaymentProvider;
import com.intifix.modules.audit.event.PaymentCompletedEvent;
import com.intifix.modules.payments.repository.PagoRepository;
import com.intifix.modules.payments.service.interfaces.FacturaService;
import com.intifix.modules.payments.service.interfaces.PagoService;
import com.intifix.shared.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final PagoMapper pagoMapper;
    private final ServiceGateway serviceGateway;
    private final PaymentProvider paymentProvider;
    private final ApplicationEventPublisher eventPublisher;
    private final FacturaService facturaService;

    @Override
    @Transactional
    public PagoResponse crearPago(CrearPagoRequest request) {
        UUID idCliente = SecurityUtils.currentUserId();
        log.info("Creando pago para servicio: {} por cliente: {}", request.getIdServicio(), idCliente);

        ServiceGateway.ServiceInfo servicio = serviceGateway.findById(request.getIdServicio())
                .orElseThrow(() -> new PagoNoEncontradoException("Servicio no encontrado: " + request.getIdServicio()));

        // Ownership: solo el cliente dueño del servicio puede pagarlo.
        if (!idCliente.equals(servicio.idCliente())) {
            throw new AccessDeniedException("Solo el cliente dueño del servicio puede pagarlo");
        }
        if (!serviceGateway.isPayable(request.getIdServicio())) {
            throw new EstadoPagoInvalidoException("El servicio no está en estado pagable");
        }
        if (servicio.montoAcordado() == null) {
            throw new EstadoPagoInvalidoException("El servicio no tiene un precio acordado (cotización aceptada)");
        }
        validarNoPagoDuplicado(request.getIdServicio());

        // Integridad de montos: el total es el precio acordado (servidor),
        // nunca el enviado por el cliente. El desglose debe sumar ese total.
        BigDecimal montoTotal = servicio.montoAcordado();
        validarDesgloseMontos(request, montoTotal);

        Pago pago = pagoMapper.toEntity(request);
        pago.setMontoTotal(montoTotal);
        pago.setEstado(EstadoPago.PENDIENTE);
        // saveAndFlush para que la BD genere creado_en y Hibernate lo lea de
        // vuelta antes de publicar el evento (que incluye creadoEn).
        Pago pagoGuardado = pagoRepository.saveAndFlush(pago);

        eventPublisher.publishEvent(new PagoCreadoEvent(
                this,
                pagoGuardado.getIdPago(),
                pagoGuardado.getIdServicio(),
                pagoGuardado.getIdMetodoPago(),
                pagoGuardado.getMontoTotal(),
                pagoGuardado.getComisionPlataforma(),
                pagoGuardado.getMontoNetoTecnico(),
                pagoGuardado.getImpuestoTotal(),
                pagoGuardado.getCreadoEn()
        ));

        log.info("Pago creado exitosamente con ID: {}", pagoGuardado.getIdPago());
        return pagoMapper.toResponse(pagoGuardado);
    }

    @Override
    @Transactional
    public PagoDetalleResponse procesarPago(ProcesarPagoRequest request) {
        log.info("Procesando pago: {}", request.getIdPago());

        Pago pago = pagoRepository.findById(request.getIdPago())
                .orElseThrow(() -> new PagoNoEncontradoException(request.getIdPago()));

        verificarPropietarioPago(pago);

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new EstadoPagoInvalidoException(
                    pago.getEstado().name(),
                    "PROCESAR"
            );
        }

        PaymentProvider.PaymentRequest paymentRequest = new PaymentProvider.PaymentRequest(
                pago.getIdServicio(),
                pago.getIdMetodoPago(),
                pago.getMontoTotal(),
                request.getDescripcion(),
                request.getMetadata()
        );

        PaymentProvider.PaymentResult resultado = paymentProvider.procesarPago(paymentRequest);

        if (resultado.exitoso()) {
            pago.setTransactionId(resultado.transactionId());
            pago.setEstado(EstadoPago.PAGADO);
            pago.setFechaPago(ZonedDateTime.now());
            Pago pagoActualizado = pagoRepository.save(pago);

            eventPublisher.publishEvent(new PagoConfirmadoEvent(
                    this,
                    pagoActualizado.getIdPago(),
                    pagoActualizado.getIdServicio(),
                    pagoActualizado.getTransactionId(),
                    pagoActualizado.getMontoTotal(),
                    pagoActualizado.getFechaPago()
            ));

            eventPublisher.publishEvent(new PaymentCompletedEvent(
                    pagoActualizado.getIdPago(),
                    pagoActualizado.getIdServicio(),
                    SecurityUtils.currentUserId(),
                    pagoActualizado.getMontoTotal()
            ));

            generarFacturaAutomatica(pagoActualizado);

            log.info("Pago procesado exitosamente: {}", pagoActualizado.getIdPago());
            return construirPagoDetalleResponse(pagoActualizado);
        } else {
            pago.setEstado(EstadoPago.FALLIDO);
            pagoRepository.save(pago);

            eventPublisher.publishEvent(new PagoFallidoEvent(
                    this,
                    pago.getIdPago(),
                    pago.getIdServicio(),
                    resultado.mensaje(),
                    pago.getMontoTotal(),
                    ZonedDateTime.now()
            ));

            throw new RuntimeException("Error al procesar pago: " + resultado.mensaje());
        }
    }

    @Override
    @Transactional
    public PagoResponse confirmarPago(UUID idPago, String transactionId) {
        log.info("Confirmando pago: {} con transactionId: {}", idPago, transactionId);

        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new PagoNoEncontradoException(idPago));

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new EstadoPagoInvalidoException(
                    pago.getEstado().name(),
                    "CONFIRMAR"
            );
        }

        pago.setTransactionId(transactionId);
        pago.setEstado(EstadoPago.PAGADO);
        pago.setFechaPago(ZonedDateTime.now());
        Pago pagoActualizado = pagoRepository.save(pago);

        eventPublisher.publishEvent(new PagoConfirmadoEvent(
                this,
                pagoActualizado.getIdPago(),
                pagoActualizado.getIdServicio(),
                pagoActualizado.getTransactionId(),
                pagoActualizado.getMontoTotal(),
                pagoActualizado.getFechaPago()
        ));

        // Confirmación puede venir de un webhook (sin usuario en contexto): userId null.
        eventPublisher.publishEvent(new PaymentCompletedEvent(
                pagoActualizado.getIdPago(),
                pagoActualizado.getIdServicio(),
                null,
                pagoActualizado.getMontoTotal()
        ));

        generarFacturaAutomatica(pagoActualizado);

        log.info("Pago confirmado exitosamente: {}", pagoActualizado.getIdPago());
        return pagoMapper.toResponse(pagoActualizado);
    }

    @Override
    @Transactional
    public PagoResponse reembolsarPago(ReembolsarPagoRequest request) {
        log.info("Reembolsando pago: {} solicitado por: {}", request.getIdPago(), SecurityUtils.currentUserId());

        Pago pago = pagoRepository.findById(request.getIdPago())
                .orElseThrow(() -> new PagoNoEncontradoException(request.getIdPago()));

        if (pago.getEstado() != EstadoPago.PAGADO) {
            throw new ReembolsoNoPermitidoException(
                    "Solo se pueden reembolsar pagos en estado PAGADO"
            );
        }

        PaymentProvider.PaymentResult resultado = paymentProvider.reembolsarPago(
                pago.getTransactionId(),
                request.getRazon()
        );

        if (resultado.exitoso()) {
            pago.setEstado(EstadoPago.REEMBOLSADO);
            Pago pagoActualizado = pagoRepository.save(pago);

            eventPublisher.publishEvent(new PagoReembolsadoEvent(
                    this,
                    pagoActualizado.getIdPago(),
                    pagoActualizado.getIdServicio(),
                    pagoActualizado.getTransactionId(),
                    request.getRazon(),
                    pagoActualizado.getMontoTotal(),
                    ZonedDateTime.now()
            ));

            log.info("Pago reembolsado exitosamente: {}", pagoActualizado.getIdPago());
            return pagoMapper.toResponse(pagoActualizado);
        } else {
            throw new RuntimeException("Error al reembolsar pago: " + resultado.mensaje());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PagoDetalleResponse obtenerPagoPorId(UUID idPago) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new PagoNoEncontradoException(idPago));
        verificarAccesoPago(pago);
        return construirPagoDetalleResponse(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public PagoDetalleResponse obtenerPagoPorServicio(UUID idServicio) {
        // "Aún no hay pago" es un estado normal (el cliente todavía no paga):
        // devolvemos null en vez de lanzar error, para que el checkout muestre
        // el formulario de pago en lugar de un toast de error.
        return pagoRepository.findByIdServicio(idServicio)
                .map(pago -> {
                    verificarAccesoPago(pago);
                    return construirPagoDetalleResponse(pago);
                })
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> listarPagos() {
        return pagoRepository.findAll().stream()
                .map(pagoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> listarPagosPorEstado(String estado) {
        EstadoPago estadoPago = EstadoPago.valueOf(estado.toUpperCase());
        return pagoRepository.findByEstado(estadoPago).stream()
                .map(pagoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenPagoResponse obtenerResumenPagos() {
        Long totalPagos = pagoRepository.count();
        Long pagosPendientes = pagoRepository.contarPagosPorEstado(EstadoPago.PENDIENTE);
        Long pagosPagados = pagoRepository.contarPagosPorEstado(EstadoPago.PAGADO);
        Long pagosReembolsados = pagoRepository.contarPagosPorEstado(EstadoPago.REEMBOLSADO);
        Long pagosFallidos = pagoRepository.contarPagosPorEstado(EstadoPago.FALLIDO);

        BigDecimal montoTotalProcesado = pagoRepository.obtenerMontoTotalProcesado(EstadoPago.PAGADO)
                .orElse(BigDecimal.ZERO);
        BigDecimal montoTotalPendiente = pagoRepository.obtenerMontoTotalProcesado(EstadoPago.PENDIENTE)
                .orElse(BigDecimal.ZERO);

        Map<EstadoPago, Long> conteoPorEstado = new EnumMap<>(EstadoPago.class);
        conteoPorEstado.put(EstadoPago.PENDIENTE, pagosPendientes);
        conteoPorEstado.put(EstadoPago.PAGADO, pagosPagados);
        conteoPorEstado.put(EstadoPago.REEMBOLSADO, pagosReembolsados);
        conteoPorEstado.put(EstadoPago.FALLIDO, pagosFallidos);

        return ResumenPagoResponse.builder()
                .totalPagos(totalPagos)
                .pagosPendientes(pagosPendientes)
                .pagosPagados(pagosPagados)
                .pagosReembolsados(pagosReembolsados)
                .pagosFallidos(pagosFallidos)
                .montoTotalProcesado(montoTotalProcesado)
                .montoTotalPendiente(montoTotalPendiente)
                .conteoPorEstado(conteoPorEstado)
                .build();
    }

    private void validarNoPagoDuplicado(UUID idServicio) {
        if (pagoRepository.existsByIdServicio(idServicio)) {
            throw new PagoDuplicadoException(idServicio);
        }
    }

    /**
     * El desglose (comisión + neto técnico + impuesto) debe sumar exactamente
     * el total autoritativo derivado del servidor. Cierra el amount tampering.
     */
    private void validarDesgloseMontos(CrearPagoRequest request, BigDecimal montoTotal) {
        BigDecimal suma = request.getComisionPlataforma()
                .add(request.getMontoNetoTecnico())
                .add(request.getImpuestoTotal());

        if (suma.compareTo(montoTotal) != 0) {
            throw new MontoInvalidoException();
        }
    }

    /**
     * Mutación de un pago: solo el cliente dueño del servicio o un ADMIN.
     */
    private void verificarPropietarioPago(Pago pago) {
        if (SecurityUtils.tieneRol("ADMIN")) {
            return;
        }
        ServiceGateway.ServiceInfo servicio = serviceGateway.findById(pago.getIdServicio())
                .orElseThrow(() -> new PagoNoEncontradoException("Servicio no encontrado: " + pago.getIdServicio()));
        if (!SecurityUtils.currentUserId().equals(servicio.idCliente())) {
            throw new AccessDeniedException("Solo el cliente dueño del servicio puede operar este pago");
        }
    }

    /**
     * Lectura de un pago: el cliente o el técnico del servicio, o un ADMIN.
     */
    private void verificarAccesoPago(Pago pago) {
        if (SecurityUtils.tieneRol("ADMIN")) {
            return;
        }
        UUID userId = SecurityUtils.currentUserId();
        ServiceGateway.ServiceInfo servicio = serviceGateway.findById(pago.getIdServicio())
                .orElseThrow(() -> new PagoNoEncontradoException("Servicio no encontrado: " + pago.getIdServicio()));
        boolean esParte = userId.equals(servicio.idCliente()) || userId.equals(servicio.idTecnico());
        if (!esParte) {
            throw new AccessDeniedException("No tiene permiso sobre este pago");
        }
    }

    private PagoDetalleResponse construirPagoDetalleResponse(Pago pago) {
        PagoDetalleResponse.PagoDetalleResponseBuilder builder = PagoDetalleResponse.builder()
                .idPago(pago.getIdPago())
                .idServicio(pago.getIdServicio())
                .idMetodoPago(pago.getIdMetodoPago())
                .montoTotal(pago.getMontoTotal())
                .comisionPlataforma(pago.getComisionPlataforma())
                .montoNetoTecnico(pago.getMontoNetoTecnico())
                .impuestoTotal(pago.getImpuestoTotal())
                .estado(pago.getEstado())
                .transactionId(pago.getTransactionId())
                .fechaPago(pago.getFechaPago())
                .creadoEn(pago.getCreadoEn());

        return builder.build();
    }

    private void generarFacturaAutomatica(Pago pago) {
        try {
            // Emite y PERSISTE la boleta del pago (el propio crearFactura publica el
            // FacturaEmitidaEvent). Si ya existe, no se duplica.
            facturaService.crearFactura(CrearFacturaRequest.builder()
                    .idPago(pago.getIdPago())
                    .tipo(TipoComprobante.BOLETA)
                    .build());
        } catch (Exception e) {
            log.error("Error al generar factura automática para pago: {}", pago.getIdPago(), e);
        }
    }
}
