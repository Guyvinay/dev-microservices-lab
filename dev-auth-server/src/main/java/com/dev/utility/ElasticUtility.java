package com.dev.utility;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ElasticUtility {

    public static Map<String, Object> getEmailDocumentMapping() {
        Map<String, Object> mappings = new HashMap<>();

        Map<String, Object> properties = new LinkedHashMap<>();

        // --- Keyword fields ---
        properties.put("emailTo", Map.of("type", "keyword"));
        properties.put("emailFrom", Map.of("type", "keyword"));
        properties.put("company", Map.of("type", "keyword"));
        properties.put("templateName", Map.of("type", "keyword"));
        properties.put("status", Map.of("type", "keyword"));
        properties.put("category", Map.of("type", "keyword"));
        properties.put("sentBy", Map.of("type", "keyword"));
        properties.put("environment", Map.of("type", "keyword"));
        properties.put("attachmentNames", Map.of("type", "keyword"));

        // --- Text fields with keyword sub-field (multi-fields) ---
        properties.put("recipientName", Map.of(
                "type", "text",
                "fields", Map.of("keyword", Map.of("type", "keyword"))
        ));
        properties.put("subject", Map.of(
                "type", "text",
                "fields", Map.of("keyword", Map.of("type", "keyword"))
        ));

        // --- Text-only fields ---
        properties.put("emailTemplate", Map.of("type", "text"));
        properties.put("errorMessage", Map.of("type", "text"));
        properties.put("threadName", Map.of("type", "text"));

        // --- Object and Boolean fields ---
        properties.put("templateVariables", Map.of("type", "object"));
        properties.put("validEmail", Map.of("type", "boolean"));
        properties.put("isHtml", Map.of("type", "boolean"));
        properties.put("resendEligible", Map.of("type", "boolean"));

        // --- Numeric fields ---
        properties.put("retryCount", Map.of("type", "integer"));
        properties.put("emailSentTimes", Map.of("type", "integer"));
        properties.put("lastSentAt", Map.of("type", "long"));
        properties.put("lastUpdatedAt", Map.of("type", "long"));
        properties.put("deliveryTimeMs", Map.of("type", "long"));
        properties.put("attachmentSizeBytes", Map.of("type", "long"));

        // --- Date field with custom format ---
        properties.put("dateCreated", Map.of(
                "type", "date",
                "format", "strict_date_optional_time||epoch_millis"
        ));

        // --- Wrap properties inside "mappings"
        mappings.put("properties", properties);

        return mappings;
    }

}
