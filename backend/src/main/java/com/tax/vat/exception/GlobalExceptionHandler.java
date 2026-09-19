package com.tax.vat.exception;

import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.service.ErrorLoggerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorLoggerService errorLogger;

    public GlobalExceptionHandler(ErrorLoggerService errorLogger) {
        this.errorLogger = errorLogger;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        errorLogger.logError(request, HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        errorLogger.logError(request, HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String errorDetail = root.getMessage() != null ? root.getMessage() : ex.getMessage();
        String friendlyMessage = "Invalid Request Body / JSON Deserialization Error: " + errorDetail;
        errorLogger.logError(request, HttpStatus.BAD_REQUEST, friendlyMessage, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(friendlyMessage));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String errorDetail = root.getMessage() != null ? root.getMessage() : ex.getMessage();
        String friendlyMessage = "Database Constraint Error: " + errorDetail;
        errorLogger.logError(request, HttpStatus.CONFLICT, friendlyMessage, ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(friendlyMessage));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        String firstMessage = errors.values().stream().findFirst().orElse("Validation failed");
        errorLogger.logError(request, HttpStatus.UNPROCESSABLE_ENTITY, "Validation failed: " + errors, ex);
        ApiResponse<Map<String, String>> response = new ApiResponse<>(false, firstMessage, errors);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex, HttpServletRequest request) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String errorDetail = root.getMessage() != null ? root.getMessage() : ex.getMessage();
        String responseMessage = "Database/Server Error: " + errorDetail;
        errorLogger.logError(request, HttpStatus.INTERNAL_SERVER_ERROR, responseMessage, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(responseMessage));
    }
}
