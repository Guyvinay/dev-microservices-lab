package com.dev.utils;


import com.dev.dto.email.EmailDocument;

import java.time.Instant;

public class GrpcMapper {
    public static com.dev.utility.grpc.email.EmailDocument toProto(EmailDocument doc) {

        return com.dev.utility.grpc.email.EmailDocument.newBuilder()
                .setEmailTo(doc.getEmailTo())
                .setRecipientName(doc.getRecipientName())
                .setEmailFrom(doc.getEmailFrom())
                .setCompany(doc.getCompany())
                .setValidEmail(doc.isValidEmail())
                .setResendEligible(doc.isResendEligible())
                .setSubject(doc.getSubject())
                .setIsHtml(doc.isHtml())
                .setTemplateName(doc.getTemplateName())
                .putAllTemplateVariables(
                        doc.getTemplateVariables()
                )
                .setStatus(doc.getStatus())
                .setRetryCount(doc.getRetryCount())
                .setEmailSentTimes(doc.getEmailSentTimes())
                .setLastSentAt(doc.getLastSentAt())
                .setLastUpdatedAt(doc.getLastUpdatedAt())
                .setDeliveryTimeMs(doc.getDeliveryTimeMs())
                .setDateCreatedEpochMs(
                        doc.getDateCreated() != null
                                ? doc.getDateCreated().toEpochMilli()
                                : 0
                )
                .build();
    }

    public static EmailDocument fromProto(com.dev.utility.grpc.email.EmailDocument proto) {

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
        doc.setTemplateVariables(proto.getTemplateVariablesMap());
        doc.setStatus(proto.getStatus());
        doc.setRetryCount(proto.getRetryCount());
        doc.setEmailSentTimes(proto.getEmailSentTimes());
        doc.setLastSentAt(proto.getLastSentAt());
        doc.setLastUpdatedAt(proto.getLastUpdatedAt());
        doc.setDeliveryTimeMs(proto.getDeliveryTimeMs());

        if (proto.getDateCreatedEpochMs() > 0) {
            doc.setDateCreated(
                    Instant.ofEpochMilli(proto.getDateCreatedEpochMs())
            );
        }

        return doc;
    }
}
