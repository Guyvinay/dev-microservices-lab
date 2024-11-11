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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GeneralResponseDTO<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        GeneralResponseDTO<Object> generalResponseDTO =  generalResponseDTO(ex, null, HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generalResponseDTO);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgument(IllegalArgumentException ex) {
        ApiResponse<String> response = new ApiResponse<>("400", ex.getMessage(), null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponseDTO<Object>> globalExceptionHandler(Exception ex, WebRequest wb){

        GeneralResponseDTO<Object> generalResponseDTO =  generalResponseDTO(ex, null, HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generalResponseDTO);
    }

    private GeneralResponseDTO<Object> generalResponseDTO(Exception e, String errorMessage, HttpStatus status) {
        GeneralResponseDTO<Object> generalResponseDTO = new GeneralResponseDTO<>();
        generalResponseDTO.setSuccess(false);
        generalResponseDTO.setStatus(status.value());
        generalResponseDTO.setErrorMsg(StringUtils.isNotBlank(errorMessage)? errorMessage: e.getMessage());
        return generalResponseDTO;
    }
}