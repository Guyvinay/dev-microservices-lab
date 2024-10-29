package com.dev.exception;

import com.dev.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiResponse<String> response = new ApiResponse<>("404", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgument(IllegalArgumentException ex) {
        ApiResponse<String> response = new ApiResponse<>("400", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
//    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> globalExceptionHandler(Exception ex, WebRequest wb){
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(
                new ExceptionDto(
                        LocalDateTime.now(),
                        ex.getMessage(),
                        wb.getDescription(false)
                ),
                HttpStatus.BAD_REQUEST
        );
    }

  /*  @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> methodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest wb) {

        List<ObjectError> objectErrors = ex.getBindingResult().getAllErrors();
//        List<String> errors = MethodArgumentNotValidException.errorsToStringList(objectErrors);

        return new ResponseEntity<>(
                new ExceptionDto(
                        LocalDateTime.now(),
                        String.join(", "),
                        wb.getDescription(false)
                ),
                HttpStatus.BAD_REQUEST
        );

    }
*/
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionDto> noHandlerFoundException(NoHandlerFoundException ex, WebRequest wb){
        return new ResponseEntity<ExceptionDto>(
                new ExceptionDto(
                        LocalDateTime.now(),
                        "There is no handler for this endpoint: " + wb.getDescription(false),
                        ex.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}