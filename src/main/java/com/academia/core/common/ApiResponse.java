package com.academia.core.common;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private Instant timestamp;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(
                true,
                data,
                "OK",
                Instant.now()
        );
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(
                true,
                data,
                message,
                Instant.now()
        );
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(
                false,
                null,
                message,
                Instant.now()
        );
    }
}
