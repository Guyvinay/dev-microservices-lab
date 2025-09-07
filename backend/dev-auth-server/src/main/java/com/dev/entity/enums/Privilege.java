package com.dev.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum Privilege {
    MANAGE_USERS("Manage users, roles, passwords, and expiry policies.", Area.USER_MANAGEMENT),
    VIEW_REPORTS("View system reports and analytics.", Area.REPORTING),
    EDIT_CONFIGURATION("Modify system configuration settings.", Area.CONFIGURATION),
    MANAGE_PERMISSIONS("Assign and modify permissions for roles.", Area.SECURITY),
    ACCESS_AUDIT_LOGS("Access and monitor system audit logs.", Area.AUDIT_LOGS),
    MANAGE_DATA("Create, update, and delete system data.", Area.DATABASE),
    RUN_BACKUPS("Initiate system backups and restore data.", Area.BACKUP_RESTORE),
    MANAGE_INTEGRATIONS("Configure and manage external integrations/APIs.", Area.INTEGRATIONS),
    VIEW_DASHBOARD("Access the system dashboard and metrics.", Area.DASHBOARD);

    private final String description;
    private final Area area;

    Privilege(String description, Area area) {
        this.description = description;
        this.area = area;
    }

    /**
     * Returns all actions associated with this privilege.
     */
    public List<Action> getActions() {
        return Arrays.stream(Action.values())
                .filter(action -> action.getPrivilege() == this)
                .collect(Collectors.toList());
    }
}
