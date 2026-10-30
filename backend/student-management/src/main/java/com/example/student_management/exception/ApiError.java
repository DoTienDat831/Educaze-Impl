package com.example.student_management.exception;

import java.time.Instant;
import java.util.Map;

/** A consistent error body returned by the REST API. */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors
) {
}
