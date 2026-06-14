package com.intifix.modules.services.enums;

/**
 * Enum representing the possible states of a quotation in the INTIFIX platform.
 * 
 * This enum maps to the PostgreSQL enum type 'estado_cotizacion' and follows
 * the lifecycle of a quotation from submission to acceptance or rejection.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public enum EstadoCotizacion {
    
    /**
     * Quotation has been sent by technician and is awaiting client response.
     * Initial state when a technician submits a price proposal.
     */
    PENDIENTE,
    
    /**
     * Client has accepted the quotation.
     * Service can now be assigned to the technician.
     */
    ACEPTADA,
    
    /**
     * Client has rejected the quotation.
     * Technician may submit a revised quotation or service remains open.
     */
    RECHAZADA,
    
    /**
     * Quotation has expired without client response.
     * Quotations typically expire after 24-48 hours.
     */
    EXPIRADA;
}
