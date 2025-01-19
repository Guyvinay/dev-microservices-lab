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
public class UserProfileRoleInfoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID")
    @ApiModelProperty(name = "roleId", value = "Unique identifier of the role")
    private Long id;

    @Column(name = "ROLE_NAME", length = 100, nullable = false)
    @ApiModelProperty(name = "roleName", value = "Name of the role")
    private String roleName;

    @Column(name = "STATUS", nullable = false)
    @ApiModelProperty(name = "isActive", value = "Indicates if the role is active or not")
    private boolean isActive;

    @Column(name = "TENANT_ID", columnDefinition = "nvarchar(100)", nullable = false)
    @ApiModelProperty(name = "tenantIdentifier", value = "Identifier of the tenant associated with the role")
    private String tenantIdentifier;

    @Column(name = "IS_ADMIN", nullable = false)
    @ApiModelProperty(name = "adminFlag", value = "Flag indicating whether the role is an admin role")
    private boolean adminFlag;

    @Column(name = "DATE_MODIFIED", nullable = false)
    @ApiModelProperty(name = "lastUpdated", value = "Timestamp when the role was last modified")
    private Long lastUpdated;

    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    @ApiModelProperty(name = "createdOn", value = "Timestamp when the role was created")
    private Long createdOn;

    @Column(name = "DESCRIPTION", length = 255)
    @ApiModelProperty(name = "description", value = "Brief description of the role")
    private String description;

    @Column(name = "CREATED_BY")
    @ApiModelProperty(name = "createdBy", value = "User ID of the creator of this role")
    private UUID createdBy;

    @Column(name = "MODIFIED_BY")
    @ApiModelProperty(name = "modifiedBy", value = "User ID of the person who last modified this role")
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
