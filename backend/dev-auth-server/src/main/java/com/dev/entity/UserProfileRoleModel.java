package com.dev.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "USER_PROFILE_ROLE_INFO_MODEL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRoleModel {

    @Id
    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "ROLE_NAME", length = 100, nullable = false)
    private String roleName;

    @Column(name = "STATUS", nullable = false)
    private boolean isActive;

    @Column(name = "TENANT_ID", columnDefinition = "nvarchar(100)", nullable = false)
    private String tenantId;

    @Column(name = "IS_ADMIN", nullable = false)
    private boolean adminFlag;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "CREATED_AT")
    private long createdAt;

    @Column(name = "UPDATED_AT")
    private long updatedAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;
}

/**
 {
 "id": "550e8400-e29b-41d4-a716-446655440000",
 "roleId": 101,
 "privilege": "Manage_Users",
 "action": "MaintainSchema_Create",
 "area": "ADMIN_PANEL",
 "assignedAt": 1705741200000,
 "assignedBy": 1001
 }
 */
