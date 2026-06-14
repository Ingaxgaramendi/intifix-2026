package com.intifix.modules.services.gateway.impl;

import com.intifix.modules.services.gateway.PaymentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Real implementation of PaymentGateway using JDBC for cross-module communication.
 * 
 * This implementation queries the payments module tables directly within the
 * modular monolith architecture, maintaining module independence without
 * creating JPA relationships.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentGatewayImpl implements PaymentGateway {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_HAS_PAID = """
        SELECT COUNT(*) FROM pagos 
        WHERE id_servicio = ? AND estado = 'PAGADO'
        """;

    private static final String SQL_GET_PAYMENT_STATUS = """
        SELECT estado FROM pagos WHERE id_servicio = ? LIMIT 1
        """;

    private static final String SQL_IS_PAYMENT_IN_PROCESS = """
        SELECT COUNT(*) FROM pagos 
        WHERE id_servicio = ? AND estado = 'PENDIENTE'
        """;

    private static final String SQL_EXISTS_PAYMENT_METHOD = """
        SELECT COUNT(*) FROM metodo_pago WHERE id_metodo_pago = ?
        """;

    @Override
    @Transactional(readOnly = true)
    public boolean hasPaid(UUID idServicio) {
        log.debug("Checking if service has been paid: {}", idServicio);
        if (idServicio == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(SQL_HAS_PAID, Integer.class, idServicio);
        boolean hasPaid = count != null && count > 0;
        log.debug("Service has paid: {} for id: {}", hasPaid, idServicio);
        return hasPaid;
    }

    @Override
    @Transactional(readOnly = true)
    public String getPaymentStatus(UUID idServicio) {
        log.debug("Getting payment status for service: {}", idServicio);
        if (idServicio == null) {
            return null;
        }
        String status = jdbcTemplate.queryForObject(SQL_GET_PAYMENT_STATUS, String.class, idServicio);
        log.debug("Payment status: {} for service: {}", status, idServicio);
        return status;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPaymentInProcess(UUID idServicio) {
        log.debug("Checking if payment is in process for service: {}", idServicio);
        if (idServicio == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(SQL_IS_PAYMENT_IN_PROCESS, Integer.class, idServicio);
        boolean inProcess = count != null && count > 0;
        log.debug("Payment is in process: {} for service: {}", inProcess, idServicio);
        return inProcess;
    }

    @Override
    @Transactional
    public boolean initiatePayment(UUID idServicio, UUID idCotizacion, BigDecimal monto) {
        log.debug("Initiating payment for service {} with quotation {} and amount {}", 
            idServicio, idCotizacion, monto);
        
        if (idServicio == null || idCotizacion == null || monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid parameters for payment initiation");
            return false;
        }

        try {
            // Check if payment already exists
            Integer existingCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pagos WHERE id_servicio = ?", 
                Integer.class, 
                idServicio
            );

            if (existingCount != null && existingCount > 0) {
                log.warn("Payment already exists for service: {}", idServicio);
                return false;
            }

            // Insert payment record with PENDIENTE status
            // Note: This is a simplified implementation. In a real scenario, you would
            // need to calculate commission, taxes, and net amount for the technician
            int rowsInserted = jdbcTemplate.update(
                """
                INSERT INTO pagos (id_pago, id_servicio, id_metodo_pago, monto_total, 
                                 comision_plataforma, monto_neto_tecnico, impuesto_total, 
                                 estado, creado_en)
                VALUES (gen_random_uuid(), ?, (SELECT id_metodo_pago FROM metodo_pago LIMIT 1), 
                        ?, 0.00, ?, 0.00, 'PENDIENTE', CURRENT_TIMESTAMP)
                """,
                idServicio, monto, monto
            );

            boolean success = rowsInserted > 0;
            log.info("Payment initiated: {} for service: {}", success, idServicio);
            return success;

        } catch (Exception e) {
            log.error("Error initiating payment for service: {}", idServicio, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean refundPayment(UUID idServicio, String motivo) {
        log.debug("Refunding payment for service {} with reason: {}", idServicio, motivo);
        
        if (idServicio == null) {
            return false;
        }

        try {
            // Update payment status to REEMBOLSADO
            int rowsUpdated = jdbcTemplate.update(
                "UPDATE pagos SET estado = 'REEMBOLSADO' WHERE id_servicio = ? AND estado = 'PAGADO'",
                idServicio
            );

            boolean success = rowsUpdated > 0;
            log.info("Payment refunded: {} for service: {}", success, idServicio);
            return success;

        } catch (Exception e) {
            log.error("Error refunding payment for service: {}", idServicio, e);
            return false;
        }
    }
}
