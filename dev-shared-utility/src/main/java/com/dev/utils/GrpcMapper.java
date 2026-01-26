package com.dev.utils;


import com.dev.dto.email.EmailCategory;
import com.dev.dto.email.EmailDocument;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class GrpcMapper {

    public static com.dev.utility.grpc.email.EmailDocument toProto(EmailDocument doc) {
        if (doc == null) return null;

        com.dev.utility.grpc.email.EmailDocument.Builder builder =
                com.dev.utility.grpc.email.EmailDocument.newBuilder()
                        .setEmailTo(doc.getEmailTo() != null ? doc.getEmailTo() : "")
                        .setRecipientName(doc.getRecipientName() != null ? doc.getRecipientName() : "")
                        .setEmailFrom(doc.getEmailFrom() != null ? doc.getEmailFrom() : "")
                        .setCompany(doc.getCompany() != null ? doc.getCompany() : "")
                        .setValidEmail(doc.isValidEmail())
                        .setResendEligible(doc.isResendEligible())
                        .setSubject(doc.getSubject() != null ? doc.getSubject() : "")
                        .setIsHtml(doc.isHtml())
                        .setTemplateName(doc.getTemplateName() != null ? doc.getTemplateName() : "")
                        .putAllTemplateVariables(doc.getTemplateVariables() != null ? doc.getTemplateVariables() : Map.of())
                        .setStatus(doc.getStatus() != null ? doc.getStatus() : "")
                        .setRetryCount(doc.getRetryCount())
                        .setEmailSentTimes(doc.getEmailSentTimes())
                        .setLastSentAt(doc.getLastSentAt())
                        .setLastUpdatedAt(doc.getLastUpdatedAt())
                        .setDeliveryTimeMs(doc.getDeliveryTimeMs())
                        .setDateCreatedEpochMs(doc.getDateCreated() != null ? doc.getDateCreated().toEpochMilli() : 0)
                        .setCategory(doc.getCategory() != null ? String.valueOf(doc.getCategory()) : "")
                        .setEmailTemplate(doc.getEmailTemplate() != null ? doc.getEmailTemplate() : "")
                        .setSentBy(doc.getSentBy() != null ? doc.getSentBy() : "")
                        .setThreadName(doc.getThreadName() != null ? doc.getThreadName() : "")
                        .setEnvironment(doc.getEnvironment() != null ? doc.getEnvironment() : "")
                        .addAllAttachmentNames(doc.getAttachmentNames() != null ? doc.getAttachmentNames() : List.of())
                        .setAttachmentSizeBytes(doc.getAttachmentSizeBytes());

        return builder.build();
    }

    public static EmailDocument fromProto(com.dev.utility.grpc.email.EmailDocument proto) {
        if (proto == null) return null;

        EmailDocument doc = new EmailDocument();
        doc.setEmailTo(proto.getEmailTo());
        doc.setRecipientName(proto.getRecipientName());
        doc.setEmailFrom(proto.getEmailFrom());
        doc.setCompany(proto.getCompany());
        doc.setValidEmail(proto.getValidEmail());
        doc.setResendEligible(proto.getResendEligible());
        doc.setSubject(proto.getSubject());
        doc.setHtml(proto.getIsHtml());
        doc.setTemplateName(proto.getTemplateName());
        proto.getTemplateVariablesMap();
        doc.setTemplateVariables(proto.getTemplateVariablesMap());
        doc.setStatus(proto.getStatus());
        doc.setRetryCount(proto.getRetryCount());
        doc.setEmailSentTimes(proto.getEmailSentTimes());
        doc.setLastSentAt(proto.getLastSentAt());
        doc.setLastUpdatedAt(proto.getLastUpdatedAt());
        doc.setDeliveryTimeMs(proto.getDeliveryTimeMs());
        if (proto.getDateCreatedEpochMs() > 0) {
            doc.setDateCreated(Instant.ofEpochMilli(proto.getDateCreatedEpochMs()));
        }

        doc.setCategory(EmailCategory.valueOf(proto.getCategory()));
        doc.setEmailTemplate(proto.getEmailTemplate());
        doc.setSentBy(proto.getSentBy());
        doc.setThreadName(proto.getThreadName());
        doc.setEnvironment(proto.getEnvironment());
        doc.setAttachmentNames(proto.getAttachmentNamesList());
        doc.setAttachmentSizeBytes(proto.getAttachmentSizeBytes());

        return doc;
    }
}
