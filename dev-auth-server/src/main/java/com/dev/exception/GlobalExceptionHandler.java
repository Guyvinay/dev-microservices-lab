package com.dev.exception;

import com.dev.dto.exception.GeneralResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* =========================
       400 – Client Errors
       ========================= */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<GeneralResponseDTO<Object>> handleInvalidInput(InvalidInputException ex) {
        log.error("Invalid input: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    /* =========================
       401 / 403 – Security
       ========================= */
    @ExceptionHandler(JWTTokenException.class)
    public ResponseEntity<GeneralResponseDTO<Object>> handleJwtTokenException(JWTTokenException ex) {
        log.error("JWT error: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GeneralResponseDTO<Object>> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication failure: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    /* =========================
       404 – Not Found
       ========================= */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GeneralResponseDTO<Object>> handleUserNotFound(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    /* =========================
       409 – Conflict
       ========================= */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<GeneralResponseDTO<Object>> handleDuplicateResource(DuplicateResourceException ex) {
        log.error("Duplicate resource: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    /* =========================
       500 – Internal Errors
       ========================= */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GeneralResponseDTO<Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Unexpected runtime exception", ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponseDTO<Object>> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /* =========================
       Common Response Builder
       ========================= */
    private ResponseEntity<GeneralResponseDTO<Object>> buildErrorResponse(
            Exception ex,
            HttpStatus status
    ) {
        GeneralResponseDTO<Object> response = GeneralResponseDTO.builder()
                .success(false)
                .status(status.value())
                .errorMsg(ex.getMessage())
                .build();

        return ResponseEntity.status(status).body(response);
    }
/*    //TODO: to be implemented properly
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {
        ProblemDetail errorDetail = null;

        // TODO send this stack trace to an observability tool
        exception.printStackTrace();

        if (exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "The username or password is incorrect");

            return errorDetail;
        }

        if (exception instanceof AccountStatusException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The account is locked");
        }

        if (exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "You are not authorized to access this resource");
        }

        if (exception instanceof SignatureException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The JWT signature is invalid");
        }

        if (exception instanceof JOSEException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", exception.getMessage());
        }

        if (exception instanceof ParseException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), exception.getMessage());
            errorDetail.setProperty("description", exception.getMessage());
        }

        if (errorDetail == null) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
            errorDetail.setProperty("description", "Unknown internal server error.");
        }
        if (exception instanceof JWTTokenExpiredException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
            errorDetail.setProperty("description", exception.getMessage());
        }
        return errorDetail;
    }*/
}