package com.intifix.modules.geo.dto;

import java.time.Instant;
import java.util.UUID;

public record LiveLocationDto(UUID tecnicoId, double latitud, double longitud, Instant updatedAt) {
}
