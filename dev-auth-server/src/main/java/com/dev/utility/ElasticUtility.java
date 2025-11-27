package com.dev.utility;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ElasticUtility {

    public static Map<String, Object> getEmailDocumentMapping() {
        Map<String, Object> mappings = new HashMap<>();

        Map<String, Object> properties = new LinkedHashMap<>();

        Map<String, Object> keywordType = Map.of("type", "keyword");
        Map<String, Object> textType = Map.of("type", "text");
        Map<String, Object> dateType = Map.of("type", "date", "format", "strict_date_optional_time||epoch_millis");
        Map<String, Object> booleanType = Map.of("type", "boolean");
        Map<String, Object> integerType = Map.of("type", "integer");
        Map<String, Object> longType = Map.of("type", "long");

        // ===============================
        // 1. Keyword fields (Exact match)
        // ===============================
        properties.put("emailTo", keywordType);
        properties.put("emailFrom", keywordType);
        properties.put("company", keywordType);
        properties.put("templateName", keywordType);
        properties.put("status", keywordType);
        properties.put("category", keywordType);
        properties.put("sentBy", keywordType);
        properties.put("environment", keywordType);
        properties.put("attachmentNames", keywordType);
        properties.put("threadName", keywordType);

        // ===============================
        // 2. Full-text fields with keyword
        // ===============================
        properties.put("recipientName", Map.of(
                "type", "text",
                "fields", Map.of("keyword", keywordType)
        ));

        properties.put("subject", Map.of(
                "type", "text",
                "fields", Map.of("keyword", keywordType)
        ));

        // ===============================
        // 3. Full-text only
        // ===============================
        properties.put("emailTemplate", textType);

        properties.put("errorMessage", Map.of(
                "type", "text",
                "fields", Map.of("keyword", keywordType) // optional but useful
        ));

        // ===============================
        // 4. Objects & Booleans
        // ===============================
        properties.put("templateVariables", Map.of("type", "object"));
        properties.put("validEmail", booleanType);
        properties.put("isHtml", booleanType);
        properties.put("resendEligible", booleanType);

        // ===============================
        // 5. Numeric fields
        // ===============================
        properties.put("retryCount", integerType);
        properties.put("emailSentTimes", integerType);
        properties.put("lastSentAt", longType);
        properties.put("lastUpdatedAt", longType);
        properties.put("deliveryTimeMs", longType);
        properties.put("attachmentSizeBytes", longType);

        // ===============================
        // 6. Date field
        // ===============================
        properties.put("dateCreated", dateType);

        Map<String, Object> conversations = new HashMap<>();

        Map<String, Object> convProps = new HashMap<>();
        convProps.put("responseType", keywordType);
        convProps.put("response", textType);
        convProps.put("status", keywordType);
        convProps.put("responder", keywordType);
        convProps.put("contact", keywordType);
        convProps.put("dateCreated", dateType);
        convProps.put("followUpNeeded", booleanType);

        conversations.put("type", "nested");
        conversations.put("properties", convProps);

        properties.put("conversations", conversations);

        // --- Wrap properties inside "mappings"
        mappings.put("properties", properties);

        return mappings;
    }

}
