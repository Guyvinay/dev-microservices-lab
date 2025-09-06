package com.dev.entity.enums;

public enum Action {

    // Manage Users
    CREATE_USER(Privilege.Manage_Users),
    DELETE_USER(Privilege.Manage_Users),
    UPDATE_USER(Privilege.Manage_Users),
    VIEW_USERS(Privilege.Manage_Users),

    // View Reports
    GENERATE_REPORT(Privilege.View_Reports),
    EXPORT_REPORT(Privilege.View_Reports),

    // Edit Configuration
    UPDATE_CONFIG(Privilege.Edit_Configuration),
    RESET_CONFIG(Privilege.Edit_Configuration),

    // Manage Permissions
    ASSIGN_ROLE(Privilege.Manage_Permissions),
    REMOVE_ROLE(Privilege.Manage_Permissions),

    // Access Audit Logs
    VIEW_LOGS(Privilege.Access_Audit_Logs),
    EXPORT_LOGS(Privilege.Access_Audit_Logs),

    // Manage Data
    CREATE_DATA(Privilege.Manage_Data),
    UPDATE_DATA(Privilege.Manage_Data),
    DELETE_DATA(Privilege.Manage_Data),

    // Backup & Restore
    INITIATE_BACKUP(Privilege.Run_Backups),
    RESTORE_BACKUP(Privilege.Run_Backups),

    // Manage Integrations
    ADD_INTEGRATION(Privilege.Manage_Integrations),
    REMOVE_INTEGRATION(Privilege.Manage_Integrations),

    // Dashboard
    VIEW_DASHBOARD(Privilege.View_Dashboard);

    private final Privilege privilege;

    Action(Privilege privilege) {
        this.privilege = privilege;
    }

    public Privilege getPrivilege() {
        return privilege;
    }
}
