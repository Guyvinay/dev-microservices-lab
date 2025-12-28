package com.dev.dto.privilege;

import lombok.Getter;

@Getter
public enum Action {

    // Manage Users
    CREATE_USER(Privilege.MANAGE_USERS, "Create new users"),
    DELETE_USER(Privilege.MANAGE_USERS, "Delete existing users"),
    UPDATE_USER(Privilege.MANAGE_USERS, "Update user details"),
    VIEW_USERS(Privilege.MANAGE_USERS, "View user list"),

    // View Reports
    GENERATE_REPORT(Privilege.VIEW_REPORTS, "Generate new reports"),
    EXPORT_REPORT(Privilege.VIEW_REPORTS, "Export reports"),

    // Edit Configuration
    UPDATE_CONFIG(Privilege.EDIT_CONFIGURATION, "Update configuration"),
    RESET_CONFIG(Privilege.EDIT_CONFIGURATION, "Reset configuration to defaults"),

    // Manage Permissions
    ASSIGN_ROLE(Privilege.MANAGE_PERMISSIONS, "Assign roles to users"),
    REMOVE_ROLE(Privilege.MANAGE_PERMISSIONS, "Remove roles from users"),

    // Access Audit Logs
    VIEW_LOGS(Privilege.ACCESS_AUDIT_LOGS, "View audit logs"),
    EXPORT_LOGS(Privilege.ACCESS_AUDIT_LOGS, "Export audit logs"),

    // Manage Data
    CREATE_DATA(Privilege.MANAGE_DATA, "Create data entries"),
    UPDATE_DATA(Privilege.MANAGE_DATA, "Update data entries"),
    DELETE_DATA(Privilege.MANAGE_DATA, "Delete data entries"),

    // Backup & Restore
    INITIATE_BACKUP(Privilege.RUN_BACKUPS, "Start backup process"),
    RESTORE_BACKUP(Privilege.RUN_BACKUPS, "Restore from backup"),

    // Manage Integrations
    ADD_INTEGRATION(Privilege.MANAGE_INTEGRATIONS, "Add new integration"),
    REMOVE_INTEGRATION(Privilege.MANAGE_INTEGRATIONS, "Remove existing integration"),

    // Dashboard
    VIEW_DASHBOARD(Privilege.VIEW_DASHBOARD, "View system dashboard");

    private final Privilege privilege;
    private final String description;

    Action(Privilege privilege, String description) {
        this.privilege = privilege;
        this.description = description;
    }
}
