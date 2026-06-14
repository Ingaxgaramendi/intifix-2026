package com.intifix.modules.services.gateway;

import java.util.UUID;

/**
 * Gateway interface for cross-module communication with the Payments module.
 * 
 * This interface provides a clean abstraction for the services module to interact
 * with payment data without creating direct JPA relationships or coupling to the
 * payments module's internal implementation.
 * 
 * This design follows the Modular Monolith pattern and prepares for future migration
 * to microservices, where this interface would be implemented as a REST client
 * or gRPC client calling the intifix-payment-service.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public interface PaymentGateway {

    /**
     * Checks if a service has been paid for.
     * 
     * This method is used to validate payment status before allowing
     * service completion or rating.
     * 
     * @param idServicio the UUID of the service to check
     * @return true if the service has been paid, false otherwise
     */
    boolean hasPaid(UUID idServicio);

    /**
     * Gets the payment status for a service.
     * 
     * This method retrieves the current payment status for a service.
     * 
     * @param idServicio the UUID of the service
     * @return the payment status as a string, or null if no payment exists
     */
    String getPaymentStatus(UUID idServicio);

    /**
     * Checks if a payment is in process for a service.
     * 
     * This method validates that a payment is currently being processed,
     * preventing duplicate payment initiation.
     * 
     * @param idServicio the UUID of the service to check
     * @return true if a payment is in process, false otherwise
     */
    boolean isPaymentInProcess(UUID idServicio);

    /**
     * Initiates payment for a service.
     * 
     * This method triggers the payment process for a service with the
     * specified amount and quotation reference.
     * 
     * @param idServicio the UUID of the service
     * @param idCotizacion the UUID of the accepted quotation
     * @param monto the payment amount
     * @return true if payment initiation was successful, false otherwise
     */
    boolean initiatePayment(UUID idServicio, UUID idCotizacion, java.math.BigDecimal monto);

    /**
     * Refunds a payment for a service.
     * 
     * This method processes a refund for a service payment, typically
     * used when a service is cancelled or disputed.
     * 
     * @param idServicio the UUID of the service
     * @param motivo the reason for the refund
     * @return true if refund was successful, false otherwise
     */
    boolean refundPayment(UUID idServicio, String motivo);
}
