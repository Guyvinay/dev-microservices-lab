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
    private EmailCategory category;          // JOB_APPLICATION, PASSWORD_RESET
    private EmailPriority priority;   // HIGH, NORMAL, LOW

    // ==== Provider Hints (optional) ====
    private String preferredProvider; // SMTP, SES

    // ==== Audit ====
    private long createdAt;

    // ==== Some extra fields that could be specific to email type ====
    private Map<String, Object> metadata;

}
