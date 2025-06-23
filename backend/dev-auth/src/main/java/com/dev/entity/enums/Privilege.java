package com.dev.entity.enums;

import lombok.Getter;

@Getter
public enum Privilege {
    Default(""),
    Manage_Users("This privilege allows the management of users in the MDO system, the assignment of roles, their passwords, and expiry."),
    View_Reports("This privilege allows users to view system reports and analytics."),
    Edit_Configuration("This privilege allows users to modify system configuration settings."),
    Manage_Permissions("This privilege allows users to assign and modify permissions for different roles."),
    Access_Audit_Logs("This privilege grants access to system audit logs for security and compliance monitoring."),
    Manage_Data("This privilege allows users to create, update, and delete system data."),
    Run_Backups("This privilege allows users to initiate system backups and restore data."),
    Manage_Integrations("This privilege allows users to configure and manage external integrations and APIs."),
    View_Dashboard("This privilege allows users to access the system dashboard and monitor real-time metrics.");
    private String value;
    private Privilege(String value) {
        this.value = value;
    }
}
