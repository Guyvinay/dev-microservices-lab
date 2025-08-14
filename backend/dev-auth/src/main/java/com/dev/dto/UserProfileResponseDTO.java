package com.dev.dto;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponseDTO implements Serializable {

    private static final long serialVersionUID = 10000L;

    private UUID id;
    private String email;
    private String name;
    private boolean isActive;
    private long createdAt;
    private long updatedAt;
}
