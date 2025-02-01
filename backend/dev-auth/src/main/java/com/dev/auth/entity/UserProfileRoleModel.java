package com.dev.auth.entity;

import io.swagger.annotations.ApiModelProperty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "USER_PROFILE_ROLE_INFO_MODEL")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRoleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "DATE_MODIFIED", nullable = false)
    private Long lastUpdated;

    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    private Long createdOn;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "CREATED_BY")
    private UUID createdBy;

    @Column(name = "MODIFIED_BY")
    private UUID modifiedBy;
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
