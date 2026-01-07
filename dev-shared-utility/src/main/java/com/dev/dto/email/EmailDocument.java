package com.dev.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDocument {

    // ============================================================
    // 1. CORE METADATA
    // ============================================================
    private String emailTo;               // Recipient email (Unique id for the document)
    private String recipientName;         // Recipient name (for personalization)
    private String emailFrom;             // Sender email address
    private String company;               // Target company (from input CSV)
    private boolean validEmail;           // Whether email address format is valid

    // ============================================================
    // 2. EMAIL CONTENT & TEMPLATE METADATA
    // ============================================================
    private String subject;               // Email subject line
    private boolean isHtml;               // Whether HTML template was used
    private String templateName;          // Template filename (e.g., email-template.html)
    private Map<String, String> templateVariables; // Variables injected into template
    private String emailTemplate;         // Final compiled email content (HTML/Text)

    // ============================================================
    // 3. DELIVERY TRACKING & STATUS
    // ============================================================
    private String status;                  // READY | SENT | DELIVERED | FAILED | BOUNCED | DISABLED
    private String errorMessage;            // Failure reason, if any
    private int retryCount;                 // Number of resend attempts
    private int emailSentTimes;             // Number of total send attempts
    private long lastSentAt;                // Timestamp when email was actually sent
    private long lastUpdatedAt;             // Timestamp of last update (status change)
    private long deliveryTimeMs;            // Time taken to send (latency/metrics)
    private Instant dateCreated;            // Time taken to send (latency/metrics)
    private String category;                // Email category like (APPLICATION | FOLLOWUP etc.)
    private boolean resendEligible;         // // flag to allow re-sending

    // ============================================================
    // 4. ATTACHMENT METADATA
    // ============================================================
    private List<String> attachmentNames; // Attached filenames
    private long attachmentSizeBytes;     // Total attachment size (for analytics)

    // ============================================================
    // 5. SYSTEM & ENVIRONMENT INFO
    // ============================================================
    private String sentBy;                // Sender account identifier (e.g., Gmail ID)
    private String threadName;            // Async thread name (useful in async batch ops)
    private String environment;           // dev, sandbox, integration, prod
}
