package com.dev.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String code; // Use appropriate code based on response (e.g., "200" for success, "400" for client error)
    private String message; // Error or success message
    private T data; // Payload data (can be null for errors)
}