package com.dev.jooq.utility;

import org.springframework.stereotype.Component;

@Component
public class DynamicNamingStrategy {

    public String normalizeColumn(String input) {

        return input
                .toLowerCase()
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+", "_");
    }

    public String normalizeTable(String tenant,
                                 String space,
                                 String form) {

        return String.format("df_%s_%s_%s",
                tenant,
                space,
                form
        ).toLowerCase();
    }
}
