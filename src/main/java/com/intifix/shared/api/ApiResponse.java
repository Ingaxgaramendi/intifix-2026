package com.intifix.shared.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse < T > {

    private boolean success;
    private String message;
    private T data;
    @Builder.Default
    private Instant timestamp = Instant.now();

    // Constructor rápido para éxitos sin data masiva
    public static < T > ApiResponse < T > success(String message, T data) {
        return ApiResponse. < T > builder()
        .success(true)
        .message(message)
        .data(data)
        .build();
    }

    // Constructor rápido para errores operativos
    public static < T > ApiResponse < T > error(String message) {
        return ApiResponse. < T > builder()
        .success(false)
        .message(message)
        .data(null)
        .build();
    }
}
