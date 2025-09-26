package com.dev.entity.enums;

import lombok.Getter;

@Getter
public enum Area {
    ADMIN_PANEL("Administration Panel"),
    USER_MANAGEMENT("User Management"),
    DATABASE("Database"),
    REPORTING("Reporting & Analytics"),
    AUDIT_LOGS("Audit Logs"),
    CONFIGURATION("System Configuration"),
    DASHBOARD("System Dashboard"),
    INTEGRATIONS("External Integrations"),
    SECURITY("Security"),
    BACKUP_RESTORE("Backup & Restore");

    private final String displayName;

    Area(String displayName) {
        this.displayName = displayName;
    }
}
