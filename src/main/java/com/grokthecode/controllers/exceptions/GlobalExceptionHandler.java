package com.grokthecode.controllers.exceptions;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleINullPointerException(NullPointerException ex, WebRequest webRequest) {
        return createErrorResponse(ex, webRequest, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest webRequest) {
        return createErrorResponse(ex, webRequest, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest webRequest) {
        return createErrorResponse(ex, webRequest, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SyncDamCatalogException.class)
    public ResponseEntity<Map<String, Object>> handleSyncDamCatalogException(SyncDamCatalogException ex, WebRequest webRequest) {
        return createErrorResponse(ex, webRequest, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest webRequest) {
        String name = ex.getName();
        String type = ex.getRequiredType().getSimpleName();
        Object value = ex.getValue();
        String message = String.format("'%s' should be a valid '%s' and '%s' isn't", name, type, value);

        return createErrorResponse(new IllegalArgumentException(message), webRequest, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(Exception ex, WebRequest request, HttpStatus status) {
        Map<String, Object> attributes = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));
        attributes.put("status", status.value());
        attributes.put("error", status.getReasonPhrase());
        attributes.put("message", ex.getMessage());
        attributes.put("path", request.getDescription(false).substring(4));

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(attributes);
    }
}
