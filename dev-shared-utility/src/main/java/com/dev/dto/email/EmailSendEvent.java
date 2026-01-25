package com.dev.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailSendEvent {

    // ==== Message Identity ====
    private String eventId;           // UUID
    private String tenantId;
    private String correlationId;     // Trace across services

    // ==== Sender Info ====
    private String from;
    private String replyTo;
    private String senderName;

    // ==== Recipient Info ====
    private String to;
    private List<String> cc;
    private List<String> bcc;

    // ==== Content ====
    private String subject;

    private boolean html;

    // Either raw body OR template reference
    private String body;
    private String templateName;
    private Map<String, Object> templateVariables;

    // ==== Attachments (metadata only) ====
    private List<AttachmentRef> attachments;

    // ==== Classification ====
    private String category;          // INVITE, OTP, BILLING
    private EmailPriority priority;   // HIGH, NORMAL, LOW

    // ==== Provider Hints (optional) ====
    private String preferredProvider; // SMTP, SES

    // ==== Audit ====
    private Instant createdAt;

}
