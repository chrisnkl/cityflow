package com.chrisnkl.cfbackend.infrastructure.common;

import java.time.Instant;

public record ApiResponse<T>(

        int status,
        String message,
        Instant timestamp,
        T data

) {

    public static <T> ApiResponse<T> of(int status, String message, T data) {
        return new ApiResponse<>(
                status,
                message,
                Instant.now(),
                data
        );
    }

    public static <T> ApiResponse<T> of(int status, String message) {
        return new ApiResponse<>(
                status,
                message,
                Instant.now(),
                null
        );
    }

    // Success methods
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(
                200,
                message,
                Instant.now(),
                null
        );
    }
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
                200,
                message,
                Instant.now(),
                data
        );
    }
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                200,
                null,
                Instant.now(),
                data
        );
    }

    // 201 - Created
    public static <T> ApiResponse<T> created(String message) {
        return new ApiResponse<>(
                201,
                message,
                Instant.now(),
                null
        );
    }
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(
                201,
                message,
                Instant.now(),
                data
        );
    }
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(
                201,
                null,
                Instant.now(),
                data
        );
    }

    // 202 - Accepted
    public static <T> ApiResponse<T> accepted(String message) {
        return new ApiResponse<>(
                202,
                message,
                Instant.now(),
                null
        );
    }
    public static <T> ApiResponse<T> accepted(String message, T data) {
        return new ApiResponse<>(
                202,
                message,
                Instant.now(),
                data
        );
    }
    public static <T> ApiResponse<T> accepted(T data) {
        return new ApiResponse<>(
                202,
                null,
                Instant.now(),
                data
        );
    }

    // 400 - Bad Request
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(
                400,
                message,
                Instant.now(),
                null
        );
    }
    public static <T> ApiResponse<T> badRequest(String message, T data) {
        return new ApiResponse<>(
                400,
                message,
                Instant.now(),
                data
        );
    }
    public static <T> ApiResponse<T> badRequest(T data) {
        return new ApiResponse<>(
                400,
                null,
                Instant.now(),
                data
        );
    }

    // 429 - TooManyRequest
    public static <T> ApiResponse<T> tooManyRequests(String message) {
        return new ApiResponse<>(
                429,
                message,
                Instant.now(),
                null
        );
    }
    public static <T> ApiResponse<T> tooManyRequests(String message, T data) {
        return new ApiResponse<>(
                429,
                message,
                Instant.now(),
                data
        );
    }
    public static <T> ApiResponse<T> tooManyRequests(T data) {
        return new ApiResponse<>(
                429,
                null,
                Instant.now(),
                data
        );
    }

    // 503 - Service Unavailable
    public static <T> ApiResponse<T> serviceUnavailable(String message) {
        return new ApiResponse<>(
                503,
                message,
                Instant.now(),
                null
        );
    }
    public static <T> ApiResponse<T> serviceUnavailable(String message, T data) {
        return new ApiResponse<>(
                503,
                message,
                Instant.now(),
                data
        );
    }
    public static <T> ApiResponse<T> serviceUnavailable(T data) {
        return new ApiResponse<>(
                503,
                null,
                Instant.now(),
                data
        );
    }

    // 500 - Internal Server Error
    public static <T> ApiResponse<T> internalServerError(String message) {
        return new ApiResponse<>(
                500,
                message,
                Instant.now(),
                null
        );
    }
    public static <T> ApiResponse<T> internalServerError(String message, T data) {
        return new ApiResponse<>(
                500,
                message,
                Instant.now(),
                data
        );
    }
    public static <T> ApiResponse<T> internalServerError(T data) {
        return new ApiResponse<>(
                500,
                null,
                Instant.now(),
                data
        );
    }




}
