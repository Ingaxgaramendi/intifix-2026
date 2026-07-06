package com.intifix.modules.services.enums;

/**
 * Enum representing the possible states of a service in the INTIFIX platform.
 * 
 * This enum maps to the PostgreSQL enum type 'estado_servicio' and follows
 * the complete lifecycle of a service from creation to completion or cancellation.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public enum EstadoServicio {
    
    /**
     * Service has been created and is waiting for technician quotations.
     * Initial state when a client creates a new service request.
     */
    PENDIENTE,
    
    /**
     * Service is receiving quotations from technicians.
     * At least one technician has shown interest or sent a quotation.
     */
    COTIZANDO,
    
    /**
     * Service has been assigned to a specific technician.
     * A quotation has been accepted and the technician is confirmed.
     */
    ASIGNADO,
    
    /**
     * Technician is actively working on the service.
     * The service is in progress at the client's location.
     */
    EN_PROCESO,

    /**
     * Technician marked the service as done; waiting for client confirmation.
     * The client must confirm completion before the service is fully FINALIZADO.
     */
    PENDIENTE_CONFIRMACION,

    /**
     * Service has been completed successfully.
     * Ready for client review and rating.
     */
    FINALIZADO,
    
    /**
     * Service has been cancelled by client or technician.
     * Cancellation reason should be recorded in service history.
     */
    CANCELADO;
}
