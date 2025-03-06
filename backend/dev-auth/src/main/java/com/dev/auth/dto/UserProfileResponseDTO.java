package com.dev.auth.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponseDTO {
    private UUID id;
    private String email;
    private String name;
    private boolean isActive;
    private long createdAt;
    private long updatedAt;
}
