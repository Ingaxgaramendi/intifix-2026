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
    /** Machine-readable code for specific error types (e.g. ACCOUNT_SUSPENDED). Null on success. */
    private String errorCode;
    @Builder.Default
    private Instant timestamp = Instant.now();

    public static < T > ApiResponse < T > success(String message, T data) {
        return ApiResponse. < T > builder()
        .success(true)
        .message(message)
        .data(data)
        .build();
    }

    public static < T > ApiResponse < T > error(String message) {
        return ApiResponse. < T > builder()
        .success(false)
        .message(message)
        .data(null)
        .build();
    }

    public static < T > ApiResponse < T > error(String message, String errorCode) {
        return ApiResponse. < T > builder()
        .success(false)
        .message(message)
        .errorCode(errorCode)
        .data(null)
        .build();
    }
}
