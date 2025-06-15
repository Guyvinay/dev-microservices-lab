package com.dev.entity.enums;

import lombok.Getter;

@Getter
public enum Area {
    ADMIN_PANEL,
    USER_MANAGEMENT,
    DATABASE,
    REPORTING,
    AUDIT_LOGS,
    CONFIGURATION,
    DASHBOARD,
    INTEGRATIONS,
    SECURITY,
    BACKUP_RESTORE;
    private Area() {
    }
}
