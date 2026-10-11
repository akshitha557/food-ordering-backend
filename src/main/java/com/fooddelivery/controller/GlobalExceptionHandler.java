


package com.fooddelivery.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle expected API errors, such as missing customers or foods
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException ex) {

        int statusCode = ex.getStatusCode().value();

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", statusCode);
        error.put(
                "error",
                HttpStatus.valueOf(statusCode).getReasonPhrase()
        );
        error.put("message", ex.getReason());

        return ResponseEntity.status(statusCode).body(error);
    }

    // Handle request validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .filter(msg -> msg != null && !msg.isBlank())
                .findFirst()
                .orElse("Invalid input");

        error.put("message", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    // Handle unexpected errors and log the actual cause
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception ex) {

        logger.error("Unexpected error while processing API request", ex);

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
