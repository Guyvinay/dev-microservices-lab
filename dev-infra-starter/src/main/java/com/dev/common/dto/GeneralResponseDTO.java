package com.dev.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class GeneralResponseDTO<T> {

    private boolean success;

    private int status;

    private String message;

    private String errorMsg;

    private T response;

    public static <T> GeneralResponseDTO<T> ok(String message) {
        return GeneralResponseDTO.<T>builder()
                .success(Boolean.TRUE)
                .status(200)
                .message(message)
                .build();
    }

    public static <T> GeneralResponseDTO<T> ok(T response) {
        return GeneralResponseDTO.<T>builder()
                .success(Boolean.TRUE)
                .status(200)
                .response(response)
                .build();
    }
    public static <T> GeneralResponseDTO<T> ok(T response, String message) {
        return GeneralResponseDTO.<T>builder()
                .success(Boolean.TRUE)
                .status(200)
                .response(response)
                .message(message)
                .build();
    }
    public static <T> GeneralResponseDTO<T> ok(T response, String message, int status) {
        return GeneralResponseDTO.<T>builder()
                .success(Boolean.TRUE)
                .status(status)
                .response(response)
                .message(message)
                .build();
    }

    public static <T> GeneralResponseDTO<T> error(String errorMsg) {
        return GeneralResponseDTO.<T>builder()
                .success(Boolean.FALSE)
                .status(500)
                .errorMsg(errorMsg)
                .build();
    }


    public static <T> GeneralResponseDTO<T> error(String errorMsg, int status) {
        return GeneralResponseDTO.<T>builder()
                .success(Boolean.FALSE)
                .status(status)
                .errorMsg(errorMsg)
                .build();
    }

    public static <T> GeneralResponseDTO<T> error(int status) {
        return GeneralResponseDTO.<T>builder()
                .success(Boolean.TRUE)
                .status(status)
                .build();
    }

    public static <T> GeneralResponseDTO<T> status(int status) {
        return GeneralResponseDTO.<T>builder()
                .status(status)
                .build();
    }

    public GeneralResponseDTO<T> acknowledge(boolean success) {
        this.success = success;
        return this;
    }

    public GeneralResponseDTO<T> message(String message) {
        this.message = message;
        return this;
    }

    public GeneralResponseDTO<T> errorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public GeneralResponseDTO<T> response(T response) {
        this.response = response;
        return this;
    }

}
