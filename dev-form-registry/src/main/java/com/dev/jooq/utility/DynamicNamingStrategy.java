package com.dev.jooq.utility;

import org.springframework.stereotype.Component;

@Component
public class DynamicNamingStrategy {
    public String normalizeColumnName(String label) {

        return label
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_");
    }

    public String normalizeTableName(String name) {

        return name
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_");
    }
}
