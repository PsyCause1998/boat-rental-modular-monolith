package com.example.boatrental.rentals.infrastructure.api.error;

import com.example.boatrental.shared.exception.BoatRentalException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.boatrental.shared.exception.BoatRentalException.ErrorCode.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);


    // Handlers domain exceptions
    @ExceptionHandler(BoatRentalException.class)
    public ResponseEntity<ApiError> handleBoatRentalException(BoatRentalException ex, HttpServletRequest req) {
        int status = switch (ex.type()) {
            case NOT_FOUND -> 404;
            case CONFLICT -> 409;
            case VALIDATION -> 400;
            case INTERNAL -> 500;
        };

        var body = ApiError.of(
                ex.code().name(),
                ex.getMessage(),
                Instant.now(),
                req.getRequestURI()
        );

        return ResponseEntity.status(status).body(body);
    }

    // Handlers generic exceptions
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        String root = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : "";

        if (root.contains("uk_active_rental_per_boat")) {
            var body = ApiError.of(
                    ACTIVE_RENTAL_ALREADY_EXISTS.name(),
                    "Active rental already exists for this boat",
                    Instant.now(),
                    req.getRequestURI()
            );
            return ResponseEntity.status(409).body(body);
        }

        // fallback propre
        var body = ApiError.of(
                INTERNAL_ERROR.name(),
                "Data integrity error",
                Instant.now(),
                req.getRequestURI()
        );
        return ResponseEntity.status(500).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage(),
                        (a, b) -> a // si doublon, on garde le 1er
                ));

        var body = ApiError.withFields(
                VALIDATION_ERROR.name(),
                "Request validation failed",
                Instant.now(),
                req.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        var body = ApiError.of(
                INVALID_ARGUMENT.name(),
                ex.getMessage(),
                Instant.now(),
                req.getRequestURI()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error on {} {}", req.getMethod(), req.getRequestURI(), ex);

        var body = ApiError.of(
                INTERNAL_ERROR.name(),
                "Unexpected error",
                Instant.now(),
                req.getRequestURI()
        );

        return ResponseEntity.status(500).body(body);
    }
}

