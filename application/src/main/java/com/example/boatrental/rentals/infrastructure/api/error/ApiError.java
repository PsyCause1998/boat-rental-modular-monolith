package com.example.boatrental.rentals.infrastructure.api.error;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        String code,
        String message,
        Instant timestamp,
        String path,
        Map<String, String> fieldErrors
) {
    public static ApiError of(String code, String message, Instant timestamp, String path) {
        return new ApiError(code, message, timestamp, path, null);
    }

    public static ApiError withFields(String code, String message, Instant timestamp, String path, Map<String, String> fieldErrors) {
        return new ApiError(code, message, timestamp, path, fieldErrors);
    }
}
