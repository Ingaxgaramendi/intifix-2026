package com.intifix.shared.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PageRequestDto(
        @Min(0) int page,
        @Min(1) @Max(100) int size
) {
    public static PageRequestDto defaults() {
        return new PageRequestDto(0, 20);
    }

    public int offset() {
        return page * size;
    }
}
