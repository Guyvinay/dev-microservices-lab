package com.dev.exception;

import com.dev.common.dto.GeneralResponseDTO;
import com.dev.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* =========================
       404 – Resource Not Found
       ========================= */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GeneralResponseDTO<Object>> handleResourceNotFound(
            ResourceNotFoundException ex
    ) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    /* =========================
       400 – Bad Request
       ========================= */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GeneralResponseDTO<Object>> handleIllegalArgument(
            IllegalArgumentException ex
    ) {
        log.warn("Invalid argument: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    /* =========================
       404 – Invalid Endpoint
       ========================= */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<GeneralResponseDTO<Object>> handleNoHandlerFound(
            NoHandlerFoundException ex,
            WebRequest request
    ) {
        log.warn("No handler found: {}", ex.getRequestURL());

        String message = "No handler found for endpoint: " + ex.getRequestURL();
        return buildErrorResponse(message, HttpStatus.NOT_FOUND);
    }

    /* =========================
       500 – Internal Server Error
       ========================= */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponseDTO<Object>> handleGenericException(
            Exception ex
    ) {
        log.error("Unhandled exception", ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /* =========================
       Common Response Builders
       ========================= */
    private ResponseEntity<GeneralResponseDTO<Object>> buildErrorResponse(
            Exception ex,
            HttpStatus status
    ) {
        return buildErrorResponse(ex.getMessage(), status);
    }

    private ResponseEntity<GeneralResponseDTO<Object>> buildErrorResponse(
            String message,
            HttpStatus status
    ) {
        GeneralResponseDTO<Object> response = GeneralResponseDTO.builder()
                .success(false)
                .status(status.value())
                .errorMsg(message)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}