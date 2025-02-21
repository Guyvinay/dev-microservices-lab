package com.dev.auth.webSocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimestampsDTO {
    private Long sentAt;
    private Long deliveredAt;
    private Long readAt; // Can be null if unread
}
