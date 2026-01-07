package com.dev.library.webSocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String userId;
    private String username;

    public UserDTO(String username) {
        this.username = username;
        this.userId = username;
    }
}