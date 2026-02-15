package com.dev.dto.email;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailSendResult {
    private String to;
    private EmailCategory category;
    private EmailPriority priority;
    private boolean success;
    private long deliveryTimeMs;
    private String smtpMessageId;
    private String errorMessage;
    private String threadName;
}
