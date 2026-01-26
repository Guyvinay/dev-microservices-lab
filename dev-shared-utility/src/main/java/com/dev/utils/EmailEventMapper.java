package com.dev.utils;

import com.dev.dto.email.AttachmentRef;
import com.dev.dto.email.EmailDocument;
import com.dev.dto.email.EmailPriority;
import com.dev.dto.email.EmailSendEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmailEventMapper {

    public static EmailSendEvent toSendEventFromEmailDocuments(EmailDocument doc) {

        if (doc == null) {
            throw new IllegalArgumentException("EmailDocument cannot be null");
        }

        return EmailSendEvent.builder()
                // Identity
                .eventId(doc.getEmailTo()) // use emailTo OR better: separate UUID field later

                // Sender info
                .from(doc.getEmailFrom())
                .senderName(doc.getCompany())
                .replyTo(doc.getEmailFrom())

                // Recipient
                .to(doc.getEmailTo())

                // Content
                .subject(doc.getSubject())
                .html(doc.isHtml())
                .body(doc.getEmailTemplate())
                .templateName(doc.getTemplateName())
                .templateVariables(
                        doc.getTemplateVariables() != null
                                ? new HashMap<>(doc.getTemplateVariables())
                                : Map.of()
                )
                .attachments(getAttachments(doc.getAttachmentNames()))

                // Classification
                .category(doc.getCategory())
                .priority(EmailPriority.NORMAL)

                // Audit
                .createdAt(doc.getLastSentAt())

                .build();
    }

    private static List<AttachmentRef> getAttachments(List<String> attachmentNames) {
        return attachmentNames.stream().map((attachment)->
                AttachmentRef.builder().fileName(attachment).contentType("PDF").storagePath("").sizeBytes(0).build()
        ).collect(Collectors.toList());
    }
}
