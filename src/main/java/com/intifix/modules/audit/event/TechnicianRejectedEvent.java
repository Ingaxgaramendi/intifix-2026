package com.intifix.modules.audit.event;

import java.util.UUID;

public record TechnicianRejectedEvent(
        UUID technicianId,
        UUID rejectedBy
) {}
