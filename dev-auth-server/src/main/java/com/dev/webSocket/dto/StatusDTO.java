package com.dev.webSocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusDTO {
    private String userId;   // For group messages, tracks per-user status
    private boolean delivered;
    private boolean read;
}
