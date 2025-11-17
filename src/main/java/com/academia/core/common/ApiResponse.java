package com.academia.core.common;

import java.time.Instant;

public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private Instant timestamp;

    public ApiResponse(boolean success, T data, String message, Instant timestamp) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<T>(
                true,
                data,
                "OK",
                Instant.now()
        );
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<T>(
                true,
                data,
                message,
                Instant.now()
        );
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<T>(
                false,
                null,
                message,
                Instant.now()
        );
    }
}
