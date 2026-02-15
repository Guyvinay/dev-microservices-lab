package com.dev.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailStatusEvent {

    private String eventId;
    private EmailCategory category;
    private Map<String, Object> updateFieldMap;
}
